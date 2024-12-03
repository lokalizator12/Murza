package com.work.rest.project.murza.service;


public interface EncryptionService {
    String encrypt(String plaintext);

    String decrypt(String ciphertext);
}
