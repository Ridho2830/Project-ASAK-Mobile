<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Quiz;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    public function run(): void
    {
        // Admin user
        User::factory()->create([
            'name' => 'Admin Firebase',
            'email' => 'admin@app.com',
            'password' => bcrypt('password'),
        ]);

        // Seed semua quiz dari QuizScenario Android
        $allStages = [
            // Stage 1
            [1, 1, 1, 'ha', '', '', 10, true],
            [1, 1, 2, 'ha', '', '', 15, true],
            [1, 1, 1, 'na', '', '', 10, true],
            [1, 1, 2, 'na', '', '', 15, true],
            [1, 2, 3, 'ha', '', '', 20, true],
            [1, 2, 4, 'ha', '', '', 20, true],
            [1, 2, 3, 'na', '', '', 20, true],
            [1, 2, 4, 'na', '', '', 20, true],
            [1, 3, 5, '', 'ha', 'na', 100, true],
            [1, 3, 1, 'ca', '', '', 10, true],
            [1, 3, 2, 'ca', '', '', 15, true],
            [1, 3, 3, 'ca', '', '', 20, true],
            // Stage 2
            [2, 1, 1, 'ka', '', '', 10, true],
            [2, 1, 1, 'da', '', '', 10, true],
            [2, 1, 2, 'ka', '', '', 15, true],
            [2, 1, 2, 'da', '', '', 15, true],
            [2, 1, 5, '', 'ka', 'da', 100, true],
            [2, 2, 3, 'ka', '', '', 20, true],
            [2, 2, 4, 'ka', '', '', 20, true],
            [2, 2, 3, 'da', '', '', 20, true],
            [2, 2, 4, 'da', '', '', 20, true],
            // Stage 3
            [3, 1, 1, 'ta', '', '', 10, true],
            [3, 1, 1, 'sa', '', '', 10, true],
            [3, 1, 2, 'ta', '', '', 15, true],
            [3, 1, 2, 'sa', '', '', 15, true],
            [3, 1, 5, '', 'ta', 'sa', 100, true],
            [3, 2, 3, 'ta', '', '', 20, true],
            [3, 2, 4, 'ta', '', '', 20, true],
            [3, 2, 3, 'sa', '', '', 20, true],
            [3, 2, 4, 'sa', '', '', 20, true],
            // Stage 4
            [4, 1, 1, 'wa', '', '', 10, true],
            [4, 1, 1, 'la', '', '', 10, true],
            [4, 1, 2, 'wa', '', '', 15, true],
            [4, 1, 2, 'la', '', '', 15, true],
            [4, 1, 5, '', 'wa', 'la', 100, true],
            [4, 2, 3, 'ma', '', '', 20, true],
            [4, 2, 4, 'ma', '', '', 20, true],
            [4, 2, 3, 'ga', '', '', 20, true],
            [4, 2, 4, 'ga', '', '', 20, true],
            // Stage 5
            [5, 1, 1, 'ba', '', '', 10, true],
            [5, 1, 1, 'nga', '', '', 10, true],
            [5, 1, 2, 'ba', '', '', 15, true],
            [5, 1, 2, 'nga', '', '', 15, true],
            [5, 1, 5, '', 'ba', 'nga', 100, true],
            [5, 2, 3, 'pa', '', '', 20, true],
            [5, 2, 4, 'pa', '', '', 20, true],
            [5, 2, 3, 'ja', '', '', 20, true],
            [5, 2, 4, 'ja', '', '', 20, true],
            // Stage 6
            [6, 1, 1, 'ya', '', '', 10, true],
            [6, 1, 1, 'nya', '', '', 10, true],
            [6, 1, 2, 'ya', '', '', 15, true],
            [6, 1, 2, 'nya', '', '', 15, true],
            [6, 1, 5, '', 'ya', 'nya', 100, true],
            [6, 2, 1, 'ra', '', '', 10, true],
            [6, 2, 3, 'ra', '', '', 20, true],
            [6, 2, 4, 'ra', '', '', 20, true],
            [6, 2, 5, '', 'ha', 'ca', 100, true],
            [6, 2, 5, '', 'na', 'ra', 100, true],
        ];

        foreach ($allStages as $q) {
            Quiz::create([
                'stage' => $q[0],
                'bagian' => $q[1],
                'tipe' => $q[2],
                'huruf' => $q[3],
                'pasangan_1' => $q[4],
                'pasangan_2' => $q[5],
                'expFull' => $q[6],
                'isStreakMode' => $q[7],
            ]);
        }
    }
}
