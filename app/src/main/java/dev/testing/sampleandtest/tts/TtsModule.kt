package dev.testing.sampleandtest.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object TtsModule {

    @Provides
    @Singleton
    fun providesTTS(@ApplicationContext context: Context): TextToSpeech =
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                Log.d("TtsModule", "providesTTS: TTS initialized")
            } else {
                Log.d("TtsModule", "providesTTS: Error initializing tts module")
            }
        }
}