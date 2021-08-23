package com.skeleton.common.component.security.utils

import com.skeleton.common.component.security.properties.SecurityProperties
import com.skeleton.common.exception.DecryptionException
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by LYT to 2021/05/14
 */
@Component
class CryptoUtils(
    val securityProperties: SecurityProperties,
) {
    private val ivSpec = IvParameterSpec(ByteArray(16) { 0x00 })
    private val newKey = SecretKeySpec(securityProperties.secretKey.toByteArray(), "AES")
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    /**
     * AES 기반 인코더
     * @param value String
     * @return String
     */
    fun encode(value: String): String {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.newKey, this.ivSpec)
        return Base64.encodeBase64String(cipher.doFinal(value.toByteArray()))
    }

    /**
     *  AES 기반 디코더
     * @param String data
     * @return String
     */
    fun decode(value: String): String {
        return try {
            cipher.init(Cipher.DECRYPT_MODE, this.newKey, this.ivSpec)
            return String(cipher.doFinal(Base64.decodeBase64(value)))
        } catch (e: Exception) {
            throw DecryptionException(e.cause)
        }
    }
}