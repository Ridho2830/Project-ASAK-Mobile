<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('quizzes', function (Blueprint $table) {
            $table->id();
            $table->integer('stage');
            $table->integer('bagian');
            $table->integer('tipe');
            $table->string('huruf')->default('');
            $table->string('pasangan_1')->default('');
            $table->string('pasangan_2')->default('');
            $table->integer('expFull')->default(10);
            $table->boolean('isStreakMode')->default(true);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('quizzes');
    }
};
