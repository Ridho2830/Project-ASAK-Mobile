<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;

class AdminLoginController extends Controller
{
    public function showLoginForm()
    {
        if (Auth::check()) {
            return redirect()->route('admin.dashboard');
        }
        
        return view('auth.login');
    }

    public function login(Request $request)
    {
        $request->validate([
            'email'    => 'required|email',
            'password' => 'required|string',
        ]);

        // Langkah 1 — Laravel Auth
        if (!Auth::attempt(['email' => $request->email, 'password' => $request->password], $request->boolean('remember'))) {
            return back()->withErrors(['email' => 'Email atau password salah.'])->withInput($request->only('email'));
        }

        // Langkah 2 — Firebase Auth verify (best-effort)
        $user = Auth::user();
        if ($user->firebase_uid) {
            try {
                $firebaseAuth = app('Kreait\Firebase\Contract\Auth');
                $firebaseUser = $firebaseAuth->getUser($user->firebase_uid);
                if ($firebaseUser->disabled) {
                    Auth::logout();
                    return back()->withErrors(['email' => 'Akun Anda telah dinonaktifkan di Firebase.'])->withInput($request->only('email'));
                }
            } catch (\Exception $e) {
                // Firebase down / gagal akses — tetap izinkan login (best-effort)
                Log::warning("Firebase Auth verify gagal untuk user {$user->id}: " . $e->getMessage());
            }
        }

        // Langkah 3 — Regenerate session dan redirect
        $request->session()->regenerate();
        return redirect()->intended(route('admin.dashboard'));
    }

    public function logout(Request $request)
    {
        Auth::logout();
        
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        
        return redirect()->route('login');
    }
}
