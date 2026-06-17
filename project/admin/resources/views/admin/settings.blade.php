@extends('layouts.app')

@section('title', 'Konfigurasi - AdminPanel')
@section('page-title', 'Konfigurasi')

@section('content')
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center">
        <h2 class="text-2xl font-bold text-gray-900 mb-2">{{ $message ?? 'Halaman Konfigurasi' }}</h2>
        <p class="text-gray-500">Fitur pengaturan dan konfigurasi akan segera hadir di sini.</p>
    </div>
@endsection
