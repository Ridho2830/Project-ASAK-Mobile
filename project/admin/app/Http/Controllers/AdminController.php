<?php

namespace App\Http\Controllers;

use App\Models\Quiz;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class AdminController extends Controller
{
    // ==========================================
    // DASHBOARD
    // ==========================================
    public function dashboard()
    {
        $totalQuizzes = Quiz::count();

        $stats = [
            'total_users' => 0,
            'active_today' => 0,
            'total_data' => $totalQuizzes,
            'storage' => '2.1 GB',
        ];

        // Try to fetch users from Firebase (best-effort)
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $documents = $firestore->database()->collection('users')->documents();
            $users = [];
            foreach ($documents as $document) {
                if ($document->exists()) {
                    $data = $document->data();
                    $createdAt = $data['created_at'] ?? null;
                    if ($createdAt instanceof \Google\Cloud\Core\Timestamp) {
                        $createdAt = $createdAt->get()->format('Y-m-d H:i:s');
                    } elseif (is_numeric($createdAt)) {
                        $createdAt = date('Y-m-d H:i:s', $createdAt / 1000);
                    }
                    $users[] = [
                        'name' => $data['name'] ?? 'Unknown',
                        'email' => $data['email'] ?? '-',
                        'created_at' => $createdAt,
                        'status' => $data['status'] ?? 'active',
                    ];
                }
            }
            $stats['total_users'] = count($users);
            usort($users, fn($a, $b) => ($b['created_at'] ?? '') <=> ($a['created_at'] ?? ''));
            $latestUsers = array_slice($users, 0, 5);

            return view('admin.dashboard', ['users' => $latestUsers, 'stats' => $stats]);
        } catch (\Exception $e) {
            Log::warning('Firebase error on dashboard: ' . $e->getMessage());
            return view('admin.dashboard', [
                'users' => [],
                'stats' => $stats,
                'firebaseError' => 'Firebase tidak tersedia: ' . $e->getMessage(),
            ]);
        }
    }

    // ==========================================
    // USERS (tetap Firebase — karena user ada di Firebase Auth)
    // ==========================================
    public function users()
    {
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $documents = $firestore->database()->collection('users')->documents();
            $users = [];
            foreach ($documents as $document) {
                if ($document->exists()) {
                    $data = $document->data();
                    $data['id'] = $document->id();
                    $users[] = $data;
                }
            }
            return view('admin.users', ['users' => $users]);
        } catch (\Exception $e) {
            return view('admin.users', ['users' => [], 'error' => 'Gagal mengambil data users: ' . $e->getMessage()]);
        }
    }

    public function storeUser(Request $request)
    {
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $auth = app('Kreait\Firebase\Contract\Auth');

            $userProperties = [
                'email' => $request->email,
                'emailVerified' => false,
                'password' => $request->password ?: 'password123',
                'displayName' => $request->name,
                'disabled' => false,
            ];
            $createdUser = $auth->createUser($userProperties);

            $firestore->database()->collection('users')->document($createdUser->uid)->set([
                'uid' => $createdUser->uid,
                'name' => $request->name,
                'email' => $request->email,
                'xp' => (int)($request->xp ?? 0),
                'level' => (int)($request->level ?? 1),
                'streak' => (int)($request->streak ?? 0),
                'created_at' => time() * 1000,
            ]);

            return redirect()->route('admin.users')->with('success', 'User berhasil ditambahkan');
        } catch (\Exception $e) {
            return redirect()->route('admin.users')->with('error', 'Gagal menambah user: ' . $e->getMessage());
        }
    }

    public function updateUser(Request $request, $id)
    {
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $firestore->database()->collection('users')->document($id)->update([
                ['path' => 'name', 'value' => $request->name],
                ['path' => 'xp', 'value' => (int)($request->xp ?? 0)],
                ['path' => 'level', 'value' => (int)($request->level ?? 1)],
                ['path' => 'streak', 'value' => (int)($request->streak ?? 0)],
            ]);

            try {
                $auth = app('Kreait\Firebase\Contract\Auth');
                $auth->updateUser($id, ['displayName' => $request->name]);
            } catch (\Exception $e) {}

            return redirect()->route('admin.users')->with('success', 'User berhasil diupdate');
        } catch (\Exception $e) {
            return redirect()->route('admin.users')->with('error', 'Gagal mengupdate user: ' . $e->getMessage());
        }
    }

    public function destroyUser($id)
    {
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $auth = app('Kreait\Firebase\Contract\Auth');

            try { $auth->deleteUser($id); } catch (\Exception $e) {}
            $firestore->database()->collection('users')->document($id)->delete();

            return redirect()->route('admin.users')->with('success', 'User berhasil dihapus');
        } catch (\Exception $e) {
            return redirect()->route('admin.users')->with('error', 'Gagal menghapus user: ' . $e->getMessage());
        }
    }

    // ==========================================
    // QUIZ DATA (SQLite lokal — Firebase hanya sync)
    // ==========================================
    public function data()
    {
        $quizzes = Quiz::orderBy('stage')->orderBy('bagian')->orderBy('tipe')->get();
        return view('admin.data', ['quizzes' => $quizzes]);
    }

    public function storeData(Request $request)
    {
        $data = [
            'stage' => (int)($request->stage ?? 1),
            'bagian' => (int)($request->bagian ?? 1),
            'tipe' => (int)($request->type ?? 1),
            'expFull' => (int)($request->expFull ?? 10),
            'isStreakMode' => $request->has('isStreakMode'),
        ];

        if ($data['tipe'] == 5) {
            $data['pasangan_1'] = $request->pasangan_1;
            $data['pasangan_2'] = $request->pasangan_2;
            $data['huruf'] = '';
        } else {
            $data['huruf'] = $request->huruf;
            $data['pasangan_1'] = '';
            $data['pasangan_2'] = '';
        }

        Quiz::create($data);

        return redirect()->route('admin.data')->with('success', 'Quiz berhasil ditambahkan');
    }

    public function updateData(Request $request, $id)
    {
        $quiz = Quiz::findOrFail($id);

        $data = [
            'stage' => (int)($request->stage ?? 1),
            'bagian' => (int)($request->bagian ?? 1),
            'tipe' => (int)($request->type ?? 1),
            'expFull' => (int)($request->expFull ?? 10),
            'isStreakMode' => $request->has('isStreakMode'),
        ];

        if ($data['tipe'] == 5) {
            $data['pasangan_1'] = $request->pasangan_1;
            $data['pasangan_2'] = $request->pasangan_2;
            $data['huruf'] = '';
        } else {
            $data['huruf'] = $request->huruf;
            $data['pasangan_1'] = '';
            $data['pasangan_2'] = '';
        }

        $quiz->update($data);

        return redirect()->route('admin.data')->with('success', 'Quiz berhasil diupdate');
    }

    public function destroyData($id)
    {
        Quiz::findOrFail($id)->delete();
        return redirect()->route('admin.data')->with('success', 'Quiz berhasil dihapus');
    }

    // ==========================================
    // SYNC TO FIREBASE — Push semua quiz lokal ke Firestore
    // ==========================================
    public function syncToFirebase()
    {
        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $collection = $firestore->database()->collection('quizzes');

            // Hapus data lama di Firestore
            $existingDocs = $collection->documents();
            foreach ($existingDocs as $doc) {
                if ($doc->exists()) {
                    $doc->reference()->delete();
                }
            }

            // Push semua data lokal ke Firestore
            $quizzes = Quiz::all();
            $count = 0;
            foreach ($quizzes as $quiz) {
                $data = [
                    'stage' => $quiz->stage,
                    'bagian' => $quiz->bagian,
                    'tipe' => $quiz->tipe,
                    'huruf' => $quiz->huruf,
                    'pasangan_1' => $quiz->pasangan_1,
                    'pasangan_2' => $quiz->pasangan_2,
                    'expFull' => $quiz->expFull,
                    'isStreakMode' => $quiz->isStreakMode,
                    'created_at' => $quiz->created_at ? $quiz->created_at->timestamp * 1000 : time() * 1000,
                ];
                $collection->add($data);
                $count++;
            }

            return redirect()->route('admin.data')->with('success', "Berhasil sync {$count} quiz ke Firebase!");
        } catch (\Exception $e) {
            return redirect()->route('admin.data')->with('error', 'Gagal sync ke Firebase: ' . $e->getMessage());
        }
    }

    // ==========================================
    // SETTINGS
    // ==========================================
    public function settings()
    {
        return view('admin.settings')->with('message', 'Halaman Pengaturan (Segera Datang)');
    }
}
