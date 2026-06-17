<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class AdminController extends Controller
{
    public function dashboard()
    {
        try {
            // Resolve Firestore secara manual agar error bisa di-catch
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $usersCollection = $firestore->database()->collection('users');
            $documents = $usersCollection->documents();
            
            $total_users = 0;
            $users = [];
            
            foreach ($documents as $document) {
                if ($document->exists()) {
                    $total_users++;
                    $data = $document->data();
                    
                    // Handle timestamp properly if it's a Firestore Timestamp object
                    $createdAt = $data['created_at'] ?? null;
                    if ($createdAt instanceof \Google\Cloud\Core\Timestamp) {
                        $createdAt = $createdAt->get()->format('Y-m-d H:i:s');
                    } elseif (is_string($createdAt)) {
                        $createdAt = date('Y-m-d H:i:s', strtotime($createdAt));
                    }
                    
                    $users[] = [
                        'name' => $data['name'] ?? 'Unknown',
                        'email' => $data['email'] ?? '-',
                        'created_at' => $createdAt,
                        'status' => $data['status'] ?? 'pending',
                    ];
                }
            }

            // Sort by created_at descending
            usort($users, function($a, $b) {
                return ($b['created_at'] ?? '') <=> ($a['created_at'] ?? '');
            });

            // Top 5 newest users
            $latestUsers = array_slice($users, 0, 5);

            $stats = [
                'total_users' => $total_users,
                'active_today' => 0,
                'total_data' => $total_users,
                'storage' => '2.1 GB',
            ];

            return view('admin.dashboard', [
                'users' => $latestUsers,
                'stats' => $stats,
            ]);
        } catch (\Exception $e) {
            Log::warning('Firebase Firestore error: ' . $e->getMessage());

            $stats = [
                'total_users' => 0,
                'active_today' => 0,
                'total_data' => 0,
                'storage' => '0 GB',
            ];
            
            return view('admin.dashboard', [
                'users' => [],
                'stats' => $stats,
                'firebaseError' => 'Gagal terhubung ke Firebase: ' . $e->getMessage(),
            ]);
        }
    }

    public function users()
    {
        return view('admin.users')->with('message', 'Halaman Users (Segera Datang)');
    }

    public function data()
    {
        return view('admin.data')->with('message', 'Halaman Data (Segera Datang)');
    }

    public function settings()
    {
        return view('admin.settings')->with('message', 'Halaman Pengaturan (Segera Datang)');
    }
}
