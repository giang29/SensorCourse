package leo.me.la.lab3


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list.rcv
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class ListFragment : Fragment() {
    private val restApi : WikiRestApi = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient().newBuilder()
                .addNetworkInterceptor(
                    HttpLoggingInterceptor().also {
                        it.level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()
        )
        .build()
        .create(WikiRestApi::class.java)

    private var activity: MainActivity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        check(context is MainActivity)
        activity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayInfo(Data.presidents[0])
        rcv.apply {
            adapter = PresidentAdapter({
                displayInfo(it)
            }) { president ->
                startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .also {
                            it.data = Uri.parse(
                                "https://fi.wikipedia.org?search=${president.replace(" ", "%20")}"
                            )
                        }
                )
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun displayInfo(president: President) {
        restApi.getQueryInfo(president.name)
            .enqueue(object : Callback<WikiResponse> {
                override fun onFailure(call: Call<WikiResponse>, t: Throwable) {}

                override fun onResponse(call: Call<WikiResponse>, response: Response<WikiResponse>) {
                    response.body()?.let { res ->
                        activity?.updateTextView(
                            president.toString(),
                            res.query.searchinfo.totalhits
                        )
                    }
                }
            })
    }
}
