package com.example.internet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.*

data class Owner(val login: String)
data class Repo(val name: String, val owner: Owner, val url: String)

interface RestApi {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>
}

//리사이클러뷰를 위한 코드
class MyAdapter(val items:List<Repo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val textView = v.findViewById<TextView>(R.id.tvRepo)
        val textView2 = v.findViewById<TextView>(R.id.tvOwner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_layout, parent, false)
        val viewHolder = MyViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text= items[position].name
        holder.textView2.text = items[position].owner.login
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val api = retrofit.create(RestApi::class.java) // RestApi 인터페이스는 다음 슬라이드에 설명

        val recycleview = findViewById<RecyclerView>(R.id.recyclerview)
        recycleview.layoutManager=LinearLayoutManager(this)
        recycleview.adapter = MyAdapter(emptyList())

        val edittext = findViewById<EditText>(R.id.editUsername)
        findViewById<Button>(R.id.buttonQuery).setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val repos = api.listRepos(edittext.text.toString())
                withContext(Dispatchers.Main) {
                    recycleview.adapter = MyAdapter(repos)
                }
            }
        }
    }
}