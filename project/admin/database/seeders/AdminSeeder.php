<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        User::create([
            'name'         => 'Admin',
            'email'        => 'admin@app.com',
            'password'     => bcrypt('password123'),
            'firebase_uid' => 'voZz0i7r41ZaGOypjBUuMMLtwCn1',
        ]);
    }
}
