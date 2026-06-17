<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'AdminPanel - Firebase + Laravel 12')</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 flex h-screen overflow-hidden text-gray-800">

    <!-- Sidebar -->
    <aside class="w-64 bg-white shadow-md flex flex-col justify-between h-full z-20">
        <div>
            <!-- Brand -->
            <div class="p-6 border-b border-gray-100 flex flex-col justify-center">
                <h1 class="text-2xl font-bold text-indigo-600">AdminPanel</h1>
                <p class="text-sm text-gray-500 font-medium mt-1">Firebase + Laravel 12</p>
            </div>

            <!-- Navigation -->
            <nav class="p-4 space-y-1">
                <p class="px-4 text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 mt-4">Menu Utama</p>
                
                <a href="{{ route('admin.dashboard') }}" class="block px-4 py-2 rounded-lg transition-all duration-200 {{ request()->routeIs('admin.dashboard') ? 'bg-indigo-50 text-indigo-700 font-semibold shadow-sm' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 font-medium' }}">
                    Dashboard
                </a>
                <a href="{{ route('admin.users') }}" class="block px-4 py-2 rounded-lg transition-all duration-200 {{ request()->routeIs('admin.users') ? 'bg-indigo-50 text-indigo-700 font-semibold shadow-sm' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 font-medium' }}">
                    Users
                </a>
                <a href="{{ route('admin.data') }}" class="block px-4 py-2 rounded-lg transition-all duration-200 {{ request()->routeIs('admin.data') ? 'bg-indigo-50 text-indigo-700 font-semibold shadow-sm' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 font-medium' }}">
                    Data
                </a>

                <p class="px-4 text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 mt-6">Pengaturan</p>
                <a href="{{ route('admin.settings') }}" class="block px-4 py-2 rounded-lg transition-all duration-200 {{ request()->routeIs('admin.settings') ? 'bg-indigo-50 text-indigo-700 font-semibold shadow-sm' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 font-medium' }}">
                    Konfigurasi
                </a>
            </nav>
        </div>

        <!-- Sidebar Footer -->
        <div class="p-4 border-t border-gray-100 bg-gray-50/50">
            @auth
                <div class="mb-4 px-2">
                    <p class="text-sm font-bold text-gray-900">{{ Auth::user()->name }}</p>
                    <p class="text-xs text-gray-500 truncate mt-0.5">{{ Auth::user()->email }}</p>
                </div>
            @else
                <div class="mb-4 px-2">
                    <p class="text-sm font-bold text-gray-900">Admin User</p>
                    <p class="text-xs text-gray-500 truncate mt-0.5">admin@example.com</p>
                </div>
            @endauth
            <form action="{{ route('logout') }}" method="POST">
                @csrf
                <button type="submit" class="flex justify-center items-center w-full px-4 py-2.5 bg-white border border-red-200 text-red-600 rounded-lg hover:bg-red-50 transition-colors text-sm font-semibold shadow-sm">
                    Logout
                </button>
            </form>
        </div>
    </aside>

    <!-- Main Content Wrapper -->
    <div class="flex-1 flex flex-col h-full overflow-hidden relative">
        
        <!-- Topbar -->
        <header class="bg-white/80 backdrop-blur-md shadow-sm h-16 flex items-center justify-between px-8 z-10 sticky top-0 border-b border-gray-100">
            <h2 class="text-xl font-bold text-gray-800 tracking-tight">
                @yield('page-title', 'Dashboard')
            </h2>
            <div class="flex items-center">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-green-100 text-green-700 shadow-sm border border-green-200">
                    <span class="w-2 h-2 mr-2 bg-green-500 rounded-full animate-pulse"></span>
                    Firebase: Connected
                </span>
            </div>
        </header>

        <!-- Main Content Area -->
        <main class="flex-1 overflow-y-auto p-8 bg-slate-50">
            @if(isset($firebaseError))
                <div class="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded-r-lg shadow-sm">
                    <div class="flex items-start">
                        <div class="flex-shrink-0 mt-0.5">
                            <svg class="h-5 w-5 text-red-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        </div>
                        <div class="ml-3">
                            <h3 class="text-sm font-bold text-red-800">Koneksi Firebase Gagal</h3>
                            <p class="text-sm text-red-700 mt-1">
                                {{ $firebaseError }}
                            </p>
                        </div>
                    </div>
                </div>
            @endif

            <div class="max-w-7xl mx-auto">
                @yield('content')
            </div>
        </main>
    </div>

</body>
</html>