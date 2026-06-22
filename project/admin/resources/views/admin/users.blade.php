@extends('layouts.app')

@section('title', 'Users - AdminPanel')
@section('page-title', 'Manajemen Users')

@section('content')

@if(session('success'))
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
        <span class="block sm:inline">{{ session('success') }}</span>
    </div>
@endif
@if(session('error'))
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
        <span class="block sm:inline">{{ session('error') }}</span>
    </div>
@endif
@if(isset($error))
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
        <span class="block sm:inline">{{ $error }}</span>
    </div>
@endif

<div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
    <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-bold text-gray-900">Daftar Users</h2>
        <button onclick="openCreateModal()" class="bg-[#d4af37] hover:bg-[#c5a017] text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm flex items-center">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
            Tambah User
        </button>
    </div>

    <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
            <thead>
                <tr class="bg-gray-50 border-b border-gray-100 text-gray-500 text-sm">
                    <th class="p-4 font-medium rounded-tl-lg">Nama</th>
                    <th class="p-4 font-medium">Email</th>
                    <th class="p-4 font-medium">XP</th>
                    <th class="p-4 font-medium">Level</th>
                    <th class="p-4 font-medium">Streak</th>
                    <th class="p-4 font-medium rounded-tr-lg">Aksi</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
                @forelse($users ?? [] as $user)
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="p-4 font-medium text-gray-900">{{ $user['name'] ?? 'Unknown' }}</td>
                        <td class="p-4 text-gray-500">{{ $user['email'] ?? '-' }}</td>
                        <td class="p-4 text-gray-500">{{ $user['xp'] ?? 0 }}</td>
                        <td class="p-4 text-gray-500">{{ $user['level'] ?? 1 }}</td>
                        <td class="p-4 text-gray-500">{{ $user['streak'] ?? 0 }}</td>
                        <td class="p-4 flex gap-2">
                            <button onclick="openEditModal('{{ $user['id'] }}', '{{ $user['name'] ?? '' }}', {{ $user['xp'] ?? 0 }}, {{ $user['level'] ?? 1 }}, {{ $user['streak'] ?? 0 }})" class="text-blue-600 hover:text-blue-800 font-medium text-sm bg-blue-50 px-3 py-1 rounded-md transition-colors">Edit</button>
                            <form action="{{ route('admin.users.destroy', $user['id']) }}" method="POST" onsubmit="return confirm('Apakah Anda yakin ingin menghapus user ini?');" class="inline">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="text-red-600 hover:text-red-800 font-medium text-sm bg-red-50 px-3 py-1 rounded-md transition-colors">Hapus</button>
                            </form>
                        </td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="6" class="p-4 text-center text-gray-500">Tidak ada user ditemukan.</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Tambah User -->
<div id="createModal" class="fixed inset-0 bg-gray-900/50 hidden items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 transform transition-all">
        <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-gray-900">Tambah User Baru</h3>
            <button onclick="closeCreateModal()" class="text-gray-400 hover:text-gray-600"><svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg></button>
        </div>
        <form action="{{ route('admin.users.store') }}" method="POST">
            @csrf
            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Nama</label>
                    <input type="text" name="name" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                    <input type="email" name="email" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Password</label>
                    <input type="password" name="password" placeholder="Kosongkan untuk password default (password123)" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>
                <div class="grid grid-cols-3 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">XP</label>
                        <input type="number" name="xp" value="0" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Level</label>
                        <input type="number" name="level" value="1" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Streak</label>
                        <input type="number" name="streak" value="0" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>
            </div>
            <div class="mt-6 flex justify-end gap-3">
                <button type="button" onclick="closeCreateModal()" class="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded-lg font-medium transition-colors">Batal</button>
                <button type="submit" class="bg-[#d4af37] hover:bg-[#c5a017] text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm">Simpan</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Edit User -->
<div id="editModal" class="fixed inset-0 bg-gray-900/50 hidden items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 transform transition-all">
        <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-gray-900">Edit User</h3>
            <button onclick="closeEditModal()" class="text-gray-400 hover:text-gray-600"><svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg></button>
        </div>
        <form id="editForm" method="POST">
            @csrf
            @method('PUT')
            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Nama</label>
                    <input type="text" name="name" id="editName" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>
                <div class="grid grid-cols-3 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">XP</label>
                        <input type="number" name="xp" id="editXp" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Level</label>
                        <input type="number" name="level" id="editLevel" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Streak</label>
                        <input type="number" name="streak" id="editStreak" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>
            </div>
            <div class="mt-6 flex justify-end gap-3">
                <button type="button" onclick="closeEditModal()" class="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded-lg font-medium transition-colors">Batal</button>
                <button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm">Update</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openCreateModal() {
        document.getElementById('createModal').classList.remove('hidden');
        document.getElementById('createModal').classList.add('flex');
    }
    function closeCreateModal() {
        document.getElementById('createModal').classList.add('hidden');
        document.getElementById('createModal').classList.remove('flex');
    }
    
    function openEditModal(id, name, xp, level, streak) {
        document.getElementById('editForm').action = '/admin/users/' + id;
        document.getElementById('editName').value = name;
        document.getElementById('editXp').value = xp;
        document.getElementById('editLevel').value = level;
        document.getElementById('editStreak').value = streak;
        
        document.getElementById('editModal').classList.remove('hidden');
        document.getElementById('editModal').classList.add('flex');
    }
    function closeEditModal() {
        document.getElementById('editModal').classList.add('hidden');
        document.getElementById('editModal').classList.remove('flex');
    }
</script>
@endsection
