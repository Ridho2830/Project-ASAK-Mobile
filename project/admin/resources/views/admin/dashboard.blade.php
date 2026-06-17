@extends('layouts.app')

@section('title', 'Dashboard - AdminPanel')
@section('page-title', 'Dashboard')

@section('content')
    <!-- Stat Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Card 1 -->
        <div class="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow duration-300 p-6 border border-gray-100 flex flex-col justify-center">
            <h3 class="text-sm font-bold text-gray-500 mb-2 uppercase tracking-wide">Total Users</h3>
            <p class="text-4xl font-extrabold text-indigo-600">{{ number_format($stats['total_users']) }}</p>
        </div>
        
        <!-- Card 2 -->
        <div class="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow duration-300 p-6 border border-gray-100 flex flex-col justify-center">
            <h3 class="text-sm font-bold text-gray-500 mb-2 uppercase tracking-wide">Aktif Hari Ini</h3>
            <p class="text-4xl font-extrabold text-indigo-600">{{ number_format($stats['active_today']) }}</p>
        </div>
        
        <!-- Card 3 -->
        <div class="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow duration-300 p-6 border border-gray-100 flex flex-col justify-center">
            <h3 class="text-sm font-bold text-gray-500 mb-2 uppercase tracking-wide">Total Data</h3>
            <p class="text-4xl font-extrabold text-indigo-600">{{ number_format($stats['total_data']) }}</p>
        </div>
        
        <!-- Card 4 -->
        <div class="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow duration-300 p-6 border border-gray-100 flex flex-col justify-center">
            <h3 class="text-sm font-bold text-gray-500 mb-2 uppercase tracking-wide">Storage</h3>
            <p class="text-4xl font-extrabold text-indigo-600">{{ $stats['storage'] }}</p>
        </div>
    </div>

    <!-- Tabel User Terbaru -->
    <div class="bg-white shadow-sm hover:shadow-md transition-shadow duration-300 rounded-2xl border border-gray-100 overflow-hidden">
        <div class="px-6 py-5 border-b border-gray-100 bg-white flex justify-between items-center">
            <h3 class="text-lg font-bold text-gray-900">User Terbaru</h3>
            <a href="{{ route('admin.users') }}" class="text-sm font-semibold text-indigo-600 hover:text-indigo-800 transition-colors">Lihat Semua &rarr;</a>
        </div>
        
        <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead>
                    <tr class="bg-gray-50/50">
                        <th class="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider border-b border-gray-100">Nama</th>
                        <th class="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider border-b border-gray-100">Email</th>
                        <th class="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider border-b border-gray-100">Terdaftar Pada</th>
                        <th class="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider border-b border-gray-100">Status</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-50 bg-white">
                    @forelse($users as $user)
                        <tr class="hover:bg-slate-50 transition-colors group cursor-default">
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm font-bold text-gray-900 group-hover:text-indigo-600 transition-colors">{{ $user['name'] }}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm font-medium text-gray-500">{{ $user['email'] }}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm font-medium text-gray-500">
                                    {{ $user['created_at'] ? \Carbon\Carbon::parse($user['created_at'])->translatedFormat('d M Y, H:i') : '-' }}
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                @if($user['status'] === 'aktif')
                                    <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-bold bg-green-100 text-green-700 border border-green-200">
                                        Aktif
                                    </span>
                                @elseif($user['status'] === 'pending')
                                    <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-bold bg-yellow-100 text-yellow-700 border border-yellow-200">
                                        Pending
                                    </span>
                                @else
                                    <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-bold bg-red-100 text-red-700 border border-red-200">
                                        Nonaktif
                                    </span>
                                @endif
                            </td>
                        </tr>
                    @empty
                        <tr>
                            <td colspan="4" class="px-6 py-12 text-center">
                                <div class="flex flex-col items-center justify-center">
                                    <svg class="h-12 w-12 text-gray-300 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                    </svg>
                                    <p class="text-sm font-medium text-gray-500">Belum ada data user dari Firebase.</p>
                                </div>
                            </td>
                        </tr>
                    @endforelse
                </tbody>
            </table>
        </div>
    </div>
@endsection
