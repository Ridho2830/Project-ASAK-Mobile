@extends('layouts.app')

@section('title', 'Data - AdminPanel')
@section('page-title', 'Data')

@section('content')
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center">
        <h2 class="text-2xl font-bold text-gray-900 mb-2">{{ $message ?? 'Halaman Data' }}</h2>
        <p class="text-gray-500">Fitur manajemen data akan segera hadir di sini.</p>
    </div>
@endsection
