package edu.uw.ee523.class8

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*

class WebServiceActivity: AppCompatActivity() {
    private val TAG = "WebServiceActivity"
    private var token: String? = null

    private var mArtistIdEditText: EditText? = null
    private var mArtistNameEditText: EditText? = null

    private val SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search?type=artist&q="

    private val SPOTIFY_ARTIST_URL = "https://api.spotify.com/v1/artists/"

    private val BEATLES_ID = "3WrFJ7ztbogyGnTHbHJFl2"

    var genres: List<String> = ArrayList()
    private var genreAdapter //(this, android.R.layout.simple_list_item_1, values)
            : ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_service)
        mArtistIdEditText = findViewById(R.id.artist_id_editText) as EditText
        mArtistNameEditText = findViewById(R.id.artist_name_editText) as EditText
        Thread { getSpotifyAcessToken() }.start()
        genreAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genres)
        val genreListView = findViewById(R.id.genre_listview) as ListView
        genreListView.adapter = genreAdapter
    }

    fun getArtistIdButtonHandler(view: View?) {
        val task = GetArtistIdTask()
        task.execute(mArtistNameEditText!!.text.toString())
    }


    class doAsync() : AsyncTask<Void, Void, String>() {


        override fun doInBackground(vararg params: Void?): String? {
            return "Yeah!"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                Log.d("AsyncTest", result)
            }
        }


    }

    // Pass in a String that is the name of a band.
    // When it returns, put the Spotify artist ID in an EditText.
     inner class GetArtistIdTask :
        AsyncTask<String?, Int?, Array<String?>>() {
//        override fun onProgressUpdate(vararg values: Int) {
//            Log.i(TAG, "Making progress...")
//        }

        override fun doInBackground(vararg params: String?): Array<String?> {
            val results = arrayOfNulls<String>(2)
            var url: URL? = null
            try {
                url = URL(SPOTIFY_SEARCH_URL + params[0])
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.doInput = true
                conn.connect()

                // Read response.
                val inputStream = conn.inputStream
                val resp: String = convertStreamToString(inputStream)
                val jObject = JSONObject(resp)

                // Need artists:items:id from the json object
                val items =
                    jObject.getJSONObject("artists").getJSONArray("items") //.getString("id");
                val firstItem = items.getJSONObject(0)
                val foo = firstItem.getString("id")
                results[0] = foo
                results[1] = firstItem.getString("name")
                return results
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            results[0] = "Something went wrong"
            return results
        }

        override fun onPostExecute(result: Array<String?>) {
            super.onPostExecute(result)
            val artist_id = findViewById(R.id.artist_id_editText) as EditText
            artist_id.setText(result[0])
            val artist_name_tv = findViewById(R.id.artistNameTextView) as TextView
            artist_name_tv.text = result[1]
        }

    }

    fun getGenresForArtistId(view: View?) {
        Thread { val results = getGenresInfo(mArtistIdEditText!!.text.toString()) }.start()
    }

    private fun getGenresInfo(artistID: String): String {

        // Open the HTTP connection and send the payload
        var url: URL? = null
        try {
            url = URL(SPOTIFY_ARTIST_URL + artistID)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.doInput = true
            conn.connect()

            // Read FCM response.
            val inputStream = conn.inputStream
            val resp = convertStreamToString(inputStream)
            val jObject = JSONObject(resp)
            val jArray = jObject.getJSONArray("genres")
            val tmp: MutableList<String> = ArrayList()
            for (i in 0 until jArray.length()) {
                try {
                    tmp.add(jArray[i].toString())
                } catch (e: JSONException) {
                    // Oops
                }
            }

            // Attach a Handler to the UI (Main) thread so we can update the UI
            val h = Handler(Looper.getMainLooper())
            h.post {
                Log.e(TAG, "run: $resp")
                Toast.makeText(this@WebServiceActivity, resp, Toast.LENGTH_LONG).show()

                // Update the ListView by filling the adapter.
                genreAdapter!!.clear()
                genreAdapter!!.addAll(tmp)
            }
            return resp
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return "Something went wrong"
    }

    /**
     * Helper function
     * @param is
     * @return
     */
     fun convertStreamToString(`is`: InputStream): String {
        val s = Scanner(`is`).useDelimiter("\\A")
        return if (s.hasNext()) s.next().replace(",", ",\n") else ""
    }

    private fun getSpotifyAcessToken() {
        // POST https://accounts.spotify.com/api/token
        val TOKEN_URL = "https://accounts.spotify.com/api/token"
        var conn: HttpURLConnection? = null
        try {
            val url = URL(TOKEN_URL)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"

            // DO NOT DO THIS!!! This is bad, on my behalf. Why??
            conn!!.setRequestProperty(
                "Authorization",
                "Basic YTdhNDY5NDkxNDgzNGY5NDk4YmI1NzU1Zjg3NjA5MDU6ODVjMTljNmFhMzAzNDNhM2E1ZDk5NGUxYzBjYWZhYjI="
            )
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            //        conn.setRequestProperty();
            conn.doOutput = true
            //            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//            out.writeChars("grant_type=client_credentials");
            val out: OutputStream = BufferedOutputStream(conn.outputStream)
            val writer = BufferedWriter(
                OutputStreamWriter(
                    out, "UTF-8"
                )
            )
            writer.write("grant_type=client_credentials")
            writer.flush()
            val responseCode = conn.responseCode
            val message = conn.responseMessage
            println("responseCode: $responseCode")
            println(message)
            //            conn.setDoInput(true);
            val inputStream = conn.inputStream
            val resp = convertStreamToString(inputStream)
            val jObject = JSONObject(resp)
            println(jObject)
            token = jObject.getString("access_token")
            conn.disconnect()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        } finally {
            conn!!.disconnect()
        }
//
//        myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        myURLConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
//        myURLConnection.setRequestProperty("Content-Language", "en-US");
//
//        conn.connect();
//
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");
//        Request request = new Request.Builder()
//                .url("https://accounts.spotify.com/api/token")
//                .method("POST", body)
//                .addHeader("Authorization", "Basic YTdhNDY5NDkxNDgzNGY5NDk4YmI1NzU1Zjg3NjA5MDU6ODVjMTljNmFhMzAzNDNhM2E1ZDk5NGUxYzBjYWZhYjI=")
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .addHeader("Cookie", "__Host-device_id=AQAAjCsEMr2vLNoYc2ugvq4PwGFcs8gW_D_PQDFJIVTrrGwh_P2aNINMFR4T9IJPy1Er2ZD4u67-sH10xMf_iOnJw5R41Nujt2Q")
//                .build();
//        Response response = client.newCall(request).execute();
    }

}