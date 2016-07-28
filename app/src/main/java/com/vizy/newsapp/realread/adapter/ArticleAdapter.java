package com.vizy.newsapp.realread.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vizy.newsapp.realread.R;
import com.vizy.newsapp.realread.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList = new ArrayList<>();
    public Context context;

    public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen, parent, false);
        ArticleViewHolder articleViewHolder = new ArticleViewHolder(item);
        return articleViewHolder;
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, int position) {
        final Article article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        Picasso.with(context).load(article.getUrlToImage()).into(holder.newsImage, new Callback() {
            @Override
            public void onSuccess() {

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Drawable drawable = holder.newsImage.getDrawable();
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                bitmap, "Image Description", null);

                        Uri uri = Uri.parse(path);


                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("image/*");
                        String shareBody = "Here is the share content body";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.getTitle() + "\n\n");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getDescription());

                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

                    }
                });

            }

            @Override
            public void onError() {
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Here is the share content body";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.getTitle() + "\n\n");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getDescription());

                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });


            }
        });


        if (holder.bookmarkButton.isChecked()) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ImageView newsImage;
        private TextView title;
        private TextView description;
        private Button share;
        private ToggleButton bookmarkButton;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);
            title = (TextView) itemView.findViewById(R.id.news_heading);
            description = (TextView) itemView.findViewById(R.id.news_description);
            share = (Button) itemView.findViewById(R.id.share_news);
            bookmarkButton = (ToggleButton) itemView.findViewById(R.id.bookmark_button);
            bookmarkButton.setText(null);
            bookmarkButton.setTextOn(null);
            bookmarkButton.setTextOff(null);


        }
    }
}
