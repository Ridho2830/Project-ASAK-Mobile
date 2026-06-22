package com.unram.asakv2.ui.ar

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.unram.asakv2.R
import com.unram.asakv2.ar.ArModelMapper
import com.unram.asakv2.viewmodel.ArViewModel
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.repository.AchievementRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class ArScanFragment : Fragment() {

    private lateinit var arViewModel: ArViewModel
    private lateinit var arFragment: ArFragment
    private lateinit var tvTitle: TextView
    private val activeNodes = mutableMapOf<String, AnchorNode>()
    private lateinit var bitmapsDeferred: Deferred<Map<String, Bitmap>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arViewModel = ViewModelProvider(this)[ArViewModel::class.java]

        tvTitle = view.findViewById(R.id.tv_ar_scan_title)

        val fabDownload = view.findViewById<FloatingActionButton>(R.id.fab_download_marker)
        fabDownload.setOnClickListener {
            val url = "https://drive.google.com/drive/folders/1FaKWT6Z-SKMF2upuTA4FyudPl5aHXYBb?usp=sharing"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka link download", Toast.LENGTH_SHORT).show()
            }
        }

        arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        
        arFragment.instructionsController.isEnabled = false

        val appContext = requireContext().applicationContext
        Log.d("AR_SCAN_DEBUG", "Starting parallel marker image loading...")
        bitmapsDeferred = lifecycleScope.async(Dispatchers.IO) {
            ArModelMapper.assets.map { assetInfo ->
                async {
                    val bitmap = decodeSampledBitmapFromAsset(appContext, assetInfo.markerPath, 512)
                    if (bitmap != null) {
                        Log.d("AR_SCAN_DEBUG", "Loaded bitmap for ${assetInfo.hurufCode} (${bitmap.width}x${bitmap.height})")
                        assetInfo.hurufCode to bitmap
                    } else {
                        Log.e("AR_SCAN_DEBUG", "Failed to load bitmap for ${assetInfo.hurufCode}")
                        null
                    }
                }
            }.awaitAll().filterNotNull().toMap()
        }

        arFragment.setOnSessionConfigurationListener { session, config ->
            config.focusMode = Config.FocusMode.AUTO
            Log.d("AR_SCAN_DEBUG", "Session configuration started.")
            try {
                
                val bitmaps = runBlocking {
                    bitmapsDeferred.await()
                }
                Log.d("AR_SCAN_DEBUG", "Bitmaps loaded. Count: ${bitmaps.size}. Creating AugmentedImageDatabase...")
                val database = AugmentedImageDatabase(session)
                bitmaps.forEach { (hurufCode, bitmap) ->
                    val index = database.addImage(hurufCode, bitmap, 0.15f)
                    Log.d("AR_SCAN_DEBUG", "Added image to database: $hurufCode at index $index with physical width 0.15m")
                }
                config.augmentedImageDatabase = database
                Log.d("AR_SCAN_DEBUG", "AugmentedImageDatabase set on config successfully.")
            } catch (e: Exception) {
                Log.e("AR_SCAN_DEBUG", "Error configuring AugmentedImageDatabase", e)
            }
        }

        val arSceneView = arFragment.arSceneView
        if (arSceneView != null) {
            Log.d("AR_SCAN_DEBUG", "arSceneView is already ready. Setting up listener immediately.")
            setupArScene(arSceneView)
        } else {
            Log.d("AR_SCAN_DEBUG", "arSceneView is null. Setting up setOnViewCreatedListener.")
            arFragment.setOnViewCreatedListener { view ->
                setupArScene(view)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::arFragment.isInitialized) {
            arFragment.instructionsController.isEnabled = false
        }
    }

    private fun setupArScene(arSceneView: com.google.ar.sceneform.ArSceneView) {
        tvTitle.visibility = View.GONE
        Log.d("AR_SCAN_DEBUG", "Setting up scene update listener...")
        arSceneView.planeRenderer.isEnabled = false
        
        arSceneView.scene.addOnUpdateListener {
            val session = arSceneView.session ?: return@addOnUpdateListener
            val frame = arSceneView.arFrame ?: return@addOnUpdateListener
            
            val allImages = session.getAllTrackables(AugmentedImage::class.java)
            for (image in allImages) {
                val code = image.name
                val anchorNode = activeNodes[code]
                
                val isTracking = image.trackingState == TrackingState.TRACKING
                val isFullTracking = isTracking && 
                    image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
                
                if (isTracking && anchorNode == null) {
                    
                    Log.d("AR_SCAN_DEBUG", "Marker baru terdeteksi: $code (method=${image.trackingMethod})")
                    
                    val assetInfo = ArModelMapper.getAssetInfo(code)
                    if (assetInfo != null) {
                        val ctx = context ?: return@addOnUpdateListener
                        val newAnchorNode = AnchorNode(image.createAnchor(image.centerPose))
                        arSceneView.scene.addChild(newAnchorNode)
                        activeNodes[code] = newAnchorNode
                        Log.d("AR_SCAN_DEBUG", "Created anchor for $code. Loading model...")

                        ModelRenderable.builder()
                            .setSource(ctx, Uri.parse(assetInfo.modelPath))
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept { renderable ->
                                Log.d("AR_SCAN_DEBUG", "Model loaded for $code")
                                
                                val box = renderable.collisionShape as? com.google.ar.sceneform.collision.Box
                                val maxDim = box?.let { maxOf(it.size.x, it.size.y, it.size.z) } ?: 1.0f
                                val markerWidth = image.extentX
                                var scale = if (maxDim > 0f) markerWidth / maxDim else 1.0f

                                if (assetInfo.category == ArModelMapper.Category.AKSARA) {
                                    scale *= 0.5f
                                } else {
                                    scale *= 0.4f
                                }

                                val pivotNode = Node()
                                pivotNode.setParent(newAnchorNode)
                                pivotNode.localScale = Vector3(scale, scale, scale)

                                val modelNode = Node()
                                modelNode.renderable = renderable
                                if (box != null) {
                                    val c = box.center
                                    modelNode.localPosition = Vector3(-c.x, -c.y, -c.z)
                                }
                                modelNode.setParent(pivotNode)
                                Log.d("AR_SCAN_DEBUG", "Scale applied: $scale")
                            }
                            .exceptionally { err ->
                                Log.e("AR_SCAN_DEBUG", "Failed to load model for $code", err)
                                null
                            }
                        
                        arViewModel.onBarcodeScanned(code)

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            val achievementRepository = AchievementRepository()
                            achievementRepository.checkAndTriggerAchievements(
                                userId = currentUser.uid,
                                eventName = "penggunaan_ar",
                                customUpdate = { user ->
                                    val unlockedLet = user.unlockedLetters.toMutableList()
                                    val unlockedWis = user.unlockedWisata.toMutableList()
                                    val unlockedBud = user.unlockedBudaya.toMutableList()

                                    val assetInfo = ArModelMapper.getAssetInfo(code)
                                    if (assetInfo != null) {
                                        when (assetInfo.category) {
                                            ArModelMapper.Category.AKSARA -> {
                                                if (!unlockedLet.contains(code)) unlockedLet.add(code)
                                            }
                                            ArModelMapper.Category.BUDAYA -> {
                                                
                                                val isWisata = code == "air_terjun" || code == "pantai_kuta" || 
                                                               code == "sirkuit" || code == "desaSade" || 
                                                               code == "desa_sade" || code == "gn_rinjani" || 
                                                               code == "museum" || code == "masjid_bayan" || 
                                                               code == "makam"
                                                if (isWisata) {
                                                    if (!unlockedWis.contains(code)) unlockedWis.add(code)
                                                } else {
                                                    if (!unlockedBud.contains(code)) unlockedBud.add(code)
                                                }
                                            }
                                        }
                                    }
                                    user.copy(
                                        unlockedLetters = unlockedLet,
                                        unlockedWisata = unlockedWis,
                                        unlockedBudaya = unlockedBud
                                    )
                                }
                            ) {  }
                        }
                    }
                } else if (anchorNode != null) {
                    
                    if (isFullTracking && !anchorNode.isEnabled) {
                        anchorNode.isEnabled = true
                        Log.d("AR_SCAN_DEBUG", "Tampilkan $code (FULL_TRACKING)")
                    } else if (!isFullTracking && anchorNode.isEnabled) {
                        anchorNode.isEnabled = false
                        Log.d("AR_SCAN_DEBUG", "Sembunyikan $code (bukan FULL_TRACKING)")
                    }
                }
            }
        }
    }

    private fun decodeSampledBitmapFromAsset(
        ctx: android.content.Context,
        assetPath: String,
        targetSize: Int
    ): Bitmap? {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            ctx.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            val largestDim = maxOf(options.outWidth, options.outHeight)
            var inSampleSize = 1
            while (largestDim / inSampleSize > targetSize) {
                inSampleSize *= 2
            }
            
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            return ctx.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (e: Exception) {
            Log.e("AR_SCAN_DEBUG", "Error downsampling bitmap from asset: $assetPath", e)
            return null
        }
    }
}
