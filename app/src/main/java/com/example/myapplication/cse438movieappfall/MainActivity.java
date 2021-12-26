package com.example.myapplication.cse438movieappfall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    MovieListResponse movieResponse;
    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        RecyclerView movieRV=findViewById(R.id.movie_list_rv);
        movieRV.setLayoutManager(new GridLayoutManager(this,2));
        movieRV.setAdapter(new FirstAdapter());

        String data;


            try {
                data = run("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&page=1&api_key=3fa9058382669f72dcb18fb405b7a831&language=en-US");
                movieResponse=new Gson().fromJson(data,MovieListResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }


    }
    class FirstAdapter extends RecyclerView.Adapter<MovieViewHolder>{

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v1=LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list,parent,false);
            return new MovieViewHolder(v1);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
          holder.tV.setText(movieResponse.getResults().get(position).getTitle());
          holder.rat.setText(""+movieResponse.getResults().get(position).getVoteAverage());
            Glide
                    .with(getApplicationContext())
                    .load("https://image.tmdb.org/t/p/w500/"+movieResponse.getResults().get(position).getPosterPath())
                    .centerCrop()
                    .into(holder.iV);
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i=new Intent(MainActivity.this,DetailActivity.class);
                    i.putExtra("result",movieResponse.getResults().get(position));
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieResponse.getResults().size();
        }
    }
    class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView tV;
        ImageView iV;
        TextView rat;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tV= itemView.findViewById(R.id.Title);
            iV=itemView.findViewById(R.id.cover);
            rat=itemView.findViewById(R.id.ratings);
        }
    }


}
