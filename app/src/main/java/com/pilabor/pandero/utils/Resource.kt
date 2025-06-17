package com.pilabor.pandero.utils

import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

sealed class Resource<T>() {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(
        val message: String, val throwable: Throwable? = null, val data: T? = null
    ) : Resource<T>()
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Resource<T> {
    return try {

        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(body)
            } else {
                Resource.Error("Response body is null")
            }
        } else {
            Resource.Error("Error: ${response.code()} - ${response.message()}")
        }
    } catch (e: SocketTimeoutException) {
        Resource.Error("Timeout error: ${e.message}", e)
    } catch (e: java.net.UnknownHostException) {
        Resource.Error("Network error: ${e.message}", e)
    } catch (e: java.io.IOException) {
        Resource.Error("IO error: ${e.message}", e)
    } catch (e: HttpException) {
        Resource.Error("HTTP error: ${e.message}", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unknown error occurred", e)
    }
}