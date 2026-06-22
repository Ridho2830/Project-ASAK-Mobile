@extends('layouts.app')

@section('title', 'Data Quiz - AdminPanel')
@section('page-title', 'Manajemen Data Quiz')

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

<div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
    <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-bold text-gray-900">Daftar Soal Quiz <span class="text-sm font-normal text-gray-400">({{ count($quizzes) }} soal — Data Lokal)</span></h2>
        <div class="flex gap-3">
            <form action="{{ route('admin.data.sync') }}" method="POST" onsubmit="return confirm('Sync semua data quiz lokal ke Firebase Firestore?\nData lama di Firebase akan dihapus dan diganti data lokal.');">
                @csrf
                <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path></svg>
                    Sync ke Firebase
                </button>
            </form>
            <button onclick="openCreateModal()" class="bg-[#d4af37] hover:bg-[#c5a017] text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
                Tambah Soal
            </button>
        </div>
    </div>

    <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
            <thead>
                <tr class="bg-gray-50 border-b border-gray-100 text-gray-500 text-sm">
                    <th class="p-4 font-medium rounded-tl-lg">Stage</th>
                    <th class="p-4 font-medium">Bagian</th>
                    <th class="p-4 font-medium">Tipe</th>
                    <th class="p-4 font-medium">Soal (Huruf/Pasangan)</th>
                    <th class="p-4 font-medium">Exp Full</th>
                    <th class="p-4 font-medium">Streak Mode</th>
                    <th class="p-4 font-medium rounded-tr-lg">Aksi</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
                @forelse($quizzes as $quiz)
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="p-4 font-medium text-gray-900">Stage {{ $quiz->stage }}</td>
                        <td class="p-4 text-gray-500">Bagian {{ $quiz->bagian }}</td>
                        <td class="p-4 text-gray-500">Tipe {{ $quiz->tipe }}</td>
                        <td class="p-4 text-gray-900 font-semibold">
                            @if($quiz->tipe == 5)
                                <span class="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                                    {{ $quiz->pasangan_1 }} ↔ {{ $quiz->pasangan_2 }}
                                </span>
                            @else
                                <span class="bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded uppercase">
                                    {{ $quiz->huruf }}
                                </span>
                            @endif
                        </td>
                        <td class="p-4 text-gray-500">{{ $quiz->expFull }} XP</td>
                        <td class="p-4 text-gray-500">
                            @if($quiz->isStreakMode)
                                <span class="text-green-600 font-bold">✓ Ya</span>
                            @else
                                <span class="text-red-500">✗ Tidak</span>
                            @endif
                        </td>
                        <td class="p-4 flex gap-2">
                            <button onclick="openEditModal({{ $quiz->id }}, {{ $quiz->stage }}, {{ $quiz->bagian }}, {{ $quiz->tipe }}, '{{ $quiz->huruf }}', '{{ $quiz->pasangan_1 }}', '{{ $quiz->pasangan_2 }}', {{ $quiz->expFull }}, {{ $quiz->isStreakMode ? 'true' : 'false' }})" class="text-blue-600 hover:text-blue-800 font-medium text-sm bg-blue-50 px-3 py-1 rounded-md transition-colors">Edit</button>
                            <form action="{{ route('admin.data.destroy', $quiz->id) }}" method="POST" onsubmit="return confirm('Apakah Anda yakin ingin menghapus data ini?');" class="inline">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="text-red-600 hover:text-red-800 font-medium text-sm bg-red-50 px-3 py-1 rounded-md transition-colors">Hapus</button>
                            </form>
                        </td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="7" class="p-4 text-center text-gray-500">Tidak ada data quiz ditemukan.</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Tambah Data -->
<div id="createModal" class="fixed inset-0 bg-gray-900/50 hidden items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 transform transition-all max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-gray-900">Tambah Data Quiz</h3>
            <button onclick="closeCreateModal()" class="text-gray-400 hover:text-gray-600"><svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg></button>
        </div>
        <form action="{{ route('admin.data.store') }}" method="POST">
            @csrf
            <div class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Stage (1-6)</label>
                        <input type="number" name="stage" min="1" max="6" value="1" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Bagian (1-3)</label>
                        <input type="number" name="bagian" min="1" max="3" value="1" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Tipe Quiz (1-5)</label>
                    <select name="type" id="createType" required onchange="toggleFields('create')" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2 bg-white">
                        <option value="1">Tipe 1 (Pilih Nama dari Gambar)</option>
                        <option value="2">Tipe 2 (Pilih Gambar dari Audio)</option>
                        <option value="3">Tipe 3 (Tulis Aksara di Canvas)</option>
                        <option value="4">Tipe 4 (Voice Recognition)</option>
                        <option value="5">Tipe 5 (Pasangkan Aksara)</option>
                    </select>
                </div>

                <div id="createHurufDiv">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Huruf (Contoh: ha, na, ca)</label>
                    <input type="text" name="huruf" id="createHuruf" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>

                <div id="createPasanganDiv" class="hidden grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Pasangan 1</label>
                        <input type="text" name="pasangan_1" id="createPasangan1" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Pasangan 2</label>
                        <input type="text" name="pasangan_2" id="createPasangan2" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Exp Full</label>
                        <input type="number" name="expFull" value="10" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div class="flex items-center pt-6">
                        <input type="checkbox" name="isStreakMode" id="createStreak" checked class="h-4 w-4 text-[#d4af37] focus:ring-[#d4af37] border-gray-300 rounded">
                        <label for="createStreak" class="ml-2 block text-sm text-gray-900">Streak Mode</label>
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

<!-- Modal Edit Data -->
<div id="editModal" class="fixed inset-0 bg-gray-900/50 hidden items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 transform transition-all max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-gray-900">Edit Data Quiz</h3>
            <button onclick="closeEditModal()" class="text-gray-400 hover:text-gray-600"><svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg></button>
        </div>
        <form id="editForm" method="POST">
            @csrf
            @method('PUT')
            <div class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Stage (1-6)</label>
                        <input type="number" name="stage" id="editStage" min="1" max="6" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Bagian (1-3)</label>
                        <input type="number" name="bagian" id="editBagian" min="1" max="3" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Tipe Quiz (1-5)</label>
                    <select name="type" id="editType" required onchange="toggleFields('edit')" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2 bg-white">
                        <option value="1">Tipe 1 (Pilih Nama dari Gambar)</option>
                        <option value="2">Tipe 2 (Pilih Gambar dari Audio)</option>
                        <option value="3">Tipe 3 (Tulis Aksara di Canvas)</option>
                        <option value="4">Tipe 4 (Voice Recognition)</option>
                        <option value="5">Tipe 5 (Pasangkan Aksara)</option>
                    </select>
                </div>

                <div id="editHurufDiv">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Huruf</label>
                    <input type="text" name="huruf" id="editHuruf" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                </div>

                <div id="editPasanganDiv" class="hidden grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Pasangan 1</label>
                        <input type="text" name="pasangan_1" id="editPasangan1" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Pasangan 2</label>
                        <input type="text" name="pasangan_2" id="editPasangan2" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Exp Full</label>
                        <input type="number" name="expFull" id="editExpFull" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-[#d4af37] focus:border-[#d4af37] border p-2">
                    </div>
                    <div class="flex items-center pt-6">
                        <input type="checkbox" name="isStreakMode" id="editStreak" value="1" class="h-4 w-4 text-[#d4af37] focus:ring-[#d4af37] border-gray-300 rounded">
                        <label for="editStreak" class="ml-2 block text-sm text-gray-900">Streak Mode</label>
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
    function toggleFields(mode) {
        const typeSelect = document.getElementById(mode + 'Type');
        const hurufDiv = document.getElementById(mode + 'HurufDiv');
        const pasanganDiv = document.getElementById(mode + 'PasanganDiv');
        const hurufInput = document.getElementById(mode + 'Huruf');
        const pas1Input = document.getElementById(mode + 'Pasangan1');
        const pas2Input = document.getElementById(mode + 'Pasangan2');

        if (typeSelect.value == '5') {
            hurufDiv.classList.add('hidden');
            pasanganDiv.classList.remove('hidden');
            pasanganDiv.classList.add('grid');
            hurufInput.removeAttribute('required');
            pas1Input.setAttribute('required', 'required');
            pas2Input.setAttribute('required', 'required');
        } else {
            hurufDiv.classList.remove('hidden');
            pasanganDiv.classList.add('hidden');
            pasanganDiv.classList.remove('grid');
            hurufInput.setAttribute('required', 'required');
            pas1Input.removeAttribute('required');
            pas2Input.removeAttribute('required');
        }
    }

    function openCreateModal() {
        document.getElementById('createModal').classList.remove('hidden');
        document.getElementById('createModal').classList.add('flex');
        toggleFields('create');
    }
    function closeCreateModal() {
        document.getElementById('createModal').classList.add('hidden');
        document.getElementById('createModal').classList.remove('flex');
    }
    
    function openEditModal(id, stage, bagian, type, huruf, pasangan1, pasangan2, expFull, isStreakMode) {
        document.getElementById('editForm').action = '/admin/data/' + id;
        document.getElementById('editStage').value = stage;
        document.getElementById('editBagian').value = bagian;
        document.getElementById('editType').value = type;
        
        document.getElementById('editHuruf').value = huruf;
        document.getElementById('editPasangan1').value = pasangan1;
        document.getElementById('editPasangan2').value = pasangan2;
        
        document.getElementById('editExpFull').value = expFull;
        document.getElementById('editStreak').checked = isStreakMode;
        
        toggleFields('edit');
        
        document.getElementById('editModal').classList.remove('hidden');
        document.getElementById('editModal').classList.add('flex');
    }
    function closeEditModal() {
        document.getElementById('editModal').classList.add('hidden');
        document.getElementById('editModal').classList.remove('flex');
    }
</script>
@endsection
