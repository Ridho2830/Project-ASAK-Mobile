<?php

use Illuminate\Support\Facades\Route;

use App\Http\Controllers\AdminController;
use App\Http\Controllers\Auth\AdminLoginController;

Route::middleware('guest')->group(function () {
    Route::get('/login',  [AdminLoginController::class, 'showLoginForm'])->name('login');
    Route::post('/login', [AdminLoginController::class, 'login']);
});

Route::post('/logout', [AdminLoginController::class, 'logout'])
    ->middleware('auth')
    ->name('logout');
Route::get('/', function () {
    return redirect()->route('admin.dashboard');
});

Route::prefix('admin')->middleware('auth')->name('admin.')->group(function () {
    Route::get('/dashboard', [AdminController::class, 'dashboard'])->name('dashboard');
    Route::get('/users',     [AdminController::class, 'users'])->name('users');
    Route::get('/data',      [AdminController::class, 'data'])->name('data');
    Route::get('/settings',  [AdminController::class, 'settings'])->name('settings');
});
