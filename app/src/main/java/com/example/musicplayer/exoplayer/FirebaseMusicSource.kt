package com.example.musicplayer.exoplayer

import com.example.musicplayer.exoplayer.State.*

class FirebaseMusicSource {
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) { //Ovaj if nam govori da ili je pesma skinuta ili ima greska ali svakako se zavrsava komunikacija sa firestore
                //Za vreme izvrsavanja ovog bloka nijedan drugi thread ne moze da pristupi onReadyListeneru
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {  listener ->
                        //Ako je true ovaj listener se poziva sa true, ako je je false onda sa false
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(state == STATE_INITIALIZING)
            return true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}