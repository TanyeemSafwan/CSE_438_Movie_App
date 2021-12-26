package com.example.myapplication.cse438movieappfall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    Result result;
    CastInformation cI;
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
        setContentView(R.layout.activity_detail);
        result=(Result) getIntent().getSerializableExtra("result");
        TextView mvTitle=findViewById(R.id.movie_title);
        mvTitle.setText(result.getTitle());
        TextView mvRating=findViewById(R.id.movie_rating);
        mvRating.setText(""+result.getVoteAverage());
        ImageView poster=findViewById(R.id.poster_photo);
        Glide
                .with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/"+result.getPosterPath())
                .centerCrop()
                .into(poster);
        ImageView cover=findViewById(R.id.cover_photo);
        Glide
                .with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/"+result.getBackdropPath())
                .centerCrop()
                .into(cover);
        TextView desc=findViewById(R.id.movie_description);
        desc.setText(result.getOverview());
        ImageButton buttonHome =findViewById(R.id.back_button1);
        TextView releaseDate;
        releaseDate=findViewById(R.id.release_date);
        releaseDate.setText("Release Date: "+result.getReleaseDate());

        buttonHome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(DetailActivity.this, MainActivity.class));
            }
        });
        RatingBar rBar=findViewById(R.id.rating_bar);
        rBar.setRating(((int)result.getVoteAverage())/2);

        RecyclerView newRv=findViewById(R.id.movie_cast);
        newRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        newRv.setAdapter(new SecondAdapter());

        String castData;
        try {
            castData = run("https://api.themoviedb.org/3/movie/"+result.getId()+"/credits?api_key=3fa9058382669f72dcb18fb405b7a831");
            cI=new Gson().fromJson(castData,CastInformation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        }
    class SecondAdapter extends RecyclerView.Adapter<CastHolder>{
        @NonNull
        @Override
        public CastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v2= LayoutInflater.from(DetailActivity.this).inflate(R.layout.cast_list,parent,false);
            return new CastHolder(v2);
        }

        @Override
        public void onBindViewHolder(@NonNull CastHolder holder, int position) {
            holder.castName.setText(cI.getCast().get(position).getName());
            Glide
                    .with(getApplicationContext())
                    .load("https://image.tmdb.org/t/p/w500/"+cI.getCast().get(position).getProfilePath())
                    .centerCrop()
                    .into(holder.castImage);


        }

        @Override
        public int getItemCount() {
            return cI.getCast().size();
        }
    }
}

    class CastHolder extends RecyclerView.ViewHolder{
        TextView castName;
        ImageView castImage;

        public CastHolder(@NonNull View itemView) {
            super(itemView);
            castName=itemView.findViewById(R.id.cast_name);
            castImage=itemView.findViewById(R.id.cast_photo);


        }

    }
