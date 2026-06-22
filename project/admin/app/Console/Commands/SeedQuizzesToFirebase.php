<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Exception;

class SeedQuizzesToFirebase extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'firebase:seed-quizzes';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Seed default quiz scenarios from Android app directly to Firebase Firestore';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $this->info('Starting Quiz Seeder to Firebase...');

        $allStages = [
            1 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'ha', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ha', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'na', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'na', 'expFull' => 15, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 3, 'huruf' => 'ha', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ha', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'na', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'na', 'expFull' => 20, 'isStreakMode' => true]
                ],
                3 => [
                    ['tipe' => 5, 'pasangan_1' => 'ha', 'pasangan_2' => 'na', 'expFull' => 100, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'ca', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ca', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'ca', 'expFull' => 20, 'isStreakMode' => true]
                ]
            ],
            2 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'ka', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'da', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ka', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'da', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'ka', 'pasangan_2' => 'da', 'expFull' => 100, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 3, 'huruf' => 'ka', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ka', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'da', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'da', 'expFull' => 20, 'isStreakMode' => true]
                ]
            ],
            3 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'ta', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'sa', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ta', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'sa', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'ta', 'pasangan_2' => 'sa', 'expFull' => 100, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 3, 'huruf' => 'ta', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ta', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'sa', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'sa', 'expFull' => 20, 'isStreakMode' => true]
                ]
            ],
            4 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'wa', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'la', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'wa', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'la', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'wa', 'pasangan_2' => 'la', 'expFull' => 100, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 3, 'huruf' => 'ma', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ma', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'ga', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ga', 'expFull' => 20, 'isStreakMode' => true]
                ]
            ],
            5 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'ba', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'nga', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ba', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'nga', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'ba', 'pasangan_2' => 'nga', 'expFull' => 100, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 3, 'huruf' => 'pa', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'pa', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'ja', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ja', 'expFull' => 20, 'isStreakMode' => true]
                ]
            ],
            6 => [
                1 => [
                    ['tipe' => 1, 'huruf' => 'ya', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 1, 'huruf' => 'nya', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'ya', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 2, 'huruf' => 'nya', 'expFull' => 15, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'ya', 'pasangan_2' => 'nya', 'expFull' => 100, 'isStreakMode' => true]
                ],
                2 => [
                    ['tipe' => 1, 'huruf' => 'ra', 'expFull' => 10, 'isStreakMode' => true],
                    ['tipe' => 3, 'huruf' => 'ra', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 4, 'huruf' => 'ra', 'expFull' => 20, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'ha', 'pasangan_2' => 'ca', 'expFull' => 100, 'isStreakMode' => true],
                    ['tipe' => 5, 'pasangan_1' => 'na', 'pasangan_2' => 'ra', 'expFull' => 100, 'isStreakMode' => true]
                ]
            ]
        ];

        try {
            $firestore = app('Kreait\Firebase\Contract\Firestore');
            $collection = $firestore->database()->collection('quizzes');

            // Optional: delete existing quizzes to prevent duplicates
            $this->info('Cleaning up existing quizzes...');
            $documents = $collection->documents();
            foreach ($documents as $document) {
                if ($document->exists()) {
                    $document->reference()->delete();
                }
            }

            $this->info('Pushing new data to Firestore...');
            
            $count = 0;
            foreach ($allStages as $stageNum => $bagians) {
                foreach ($bagians as $bagianNum => $quizzes) {
                    foreach ($quizzes as $quiz) {
                        $data = [
                            'stage' => $stageNum,
                            'bagian' => $bagianNum,
                            'tipe' => $quiz['tipe'],
                            'expFull' => $quiz['expFull'],
                            'isStreakMode' => $quiz['isStreakMode'],
                            'created_at' => time() * 1000,
                        ];

                        if ($quiz['tipe'] == 5) {
                            $data['pasangan_1'] = $quiz['pasangan_1'];
                            $data['pasangan_2'] = $quiz['pasangan_2'];
                            $data['huruf'] = '';
                        } else {
                            $data['huruf'] = $quiz['huruf'];
                            $data['pasangan_1'] = '';
                            $data['pasangan_2'] = '';
                        }

                        $collection->add($data);
                        $count++;
                    }
                }
            }

            $this->info("Successfully seeded {$count} quizzes to Firebase!");
            return 0;

        } catch (Exception $e) {
            $this->error('Failed to seed: ' . $e->getMessage());
            return 1;
        }
    }
}
