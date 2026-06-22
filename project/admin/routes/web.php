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
    
    // Users CRUD
    Route::get('/users',     [AdminController::class, 'users'])->name('users');
    Route::post('/users',    [AdminController::class, 'storeUser'])->name('users.store');
    Route::put('/users/{id}', [AdminController::class, 'updateUser'])->name('users.update');
    Route::delete('/users/{id}', [AdminController::class, 'destroyUser'])->name('users.destroy');

    // Data / Quizzes CRUD
    Route::get('/data',      [AdminController::class, 'data'])->name('data');
    Route::post('/data',     [AdminController::class, 'storeData'])->name('data.store');
    Route::put('/data/{id}', [AdminController::class, 'updateData'])->name('data.update');
    Route::delete('/data/{id}', [AdminController::class, 'destroyData'])->name('data.destroy');
    Route::post('/data/sync-firebase', [AdminController::class, 'syncToFirebase'])->name('data.sync');

    Route::get('/settings',  [AdminController::class, 'settings'])->name('settings');
});
