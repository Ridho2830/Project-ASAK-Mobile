<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Quiz extends Model
{
    protected $fillable = [
        'stage',
        'bagian',
        'tipe',
        'huruf',
        'pasangan_1',
        'pasangan_2',
        'expFull',
        'isStreakMode',
    ];

    protected $casts = [
        'stage' => 'integer',
        'bagian' => 'integer',
        'tipe' => 'integer',
        'expFull' => 'integer',
        'isStreakMode' => 'boolean',
    ];
}
