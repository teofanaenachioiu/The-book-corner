package com.example.firstapp.core


import com.example.firstapp.auth.data.AuthRepository
import com.example.firstapp.book_corner.data.BookRepository
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

object WebSocketProvider {

    private var webSocket: WebSocket

    private var catRepository: BookRepository? = null;

    init {
        val request = Request.Builder().url("ws://$IpAddress:3000").build()
        webSocket = OkHttpClient().newWebSocket(request, MyWebSocketListener());
    }

    fun initRepo(catRepo: BookRepository) {
        catRepository = catRepo
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {

            println("Conexiunea cu serverul s-a deschis!")
            val token = Api.tokenInterceptor.token
            val gson = Gson()
            val json = gson.toJson({token})
            print(json);
            WebSocketProvider.webSocket.send(json)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            println("WEB SOCKET MESSAGE")
            println(text);

//            runBlocking {
//                val gson = Gson()
//                val objectList = gson.fromJson(text, Array<Book>::class.java).asList()
//                catRepository!!.refreshLocally(objectList)
//            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            println("WEB SOCKET MESSAGE")
            println(bytes);
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            println("WEB SOCKET CLOSING")
            println(code);
            println(reason);
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("WEB SOCKET FAILURE")
            t.printStackTrace();
            println("WEB SOCKET FAILURE RESPONSE")
            println(response?.message())
        }

    }
}