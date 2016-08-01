package com.jeevitharoyapathi.assignment_2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jeevitharoyapathi.assignment_2.R;
import com.jeevitharoyapathi.assignment_2.models.Article;
import com.jeevitharoyapathi.assignment_2.utils.animationUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeevitha.royapathi on 7/30/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public final static int IMAGE_VIEW = 1;
    public final static int TEXT_VIEW = 0;
    private List<Article> mDataset;
    private Context mContext;
    private OnItemClickListener mclicklistener;

    public NewsAdapter(Context myContext, List<Article> myDataset) {
        mContext = myContext;
        mDataset = myDataset;

    }

    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == IMAGE_VIEW) {
            v = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.image_news_item, parent, false);
            return new regularViewHolder(v);
        } else {
        v=LayoutInflater.from(parent.getContext()).
                inflate(R.layout.text_news_item,parent,false);
            return new textViewHolder(v);
        }

    }

    @Override
    public int getItemViewType(final int position) {
        final Article article = mDataset.get(position);
        if (isArticlWithImage(article)) {
            return IMAGE_VIEW;
        } else {
            return TEXT_VIEW;
        }
    }

    private boolean isArticlWithImage(Article article) {
        if (article.getThumbNail().isEmpty())
            return false;
        return true;
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        mclicklistener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mDataset.get(position);
        holder.bindArticleData(article);

    }

    public int getItemCount() {
        return mDataset.size();
    }

    public Object getItem(int position) {
        return mDataset.get(position);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }

        public abstract void bindArticleData(Article article);
    }

    public class regularViewHolder extends ViewHolder {
        @BindView(R.id.newsTitle)
        TextView articleTitle;
        @BindView(R.id.imgNewsIcon)
        ImageView vIcon;
        @BindView(R.id.layout_news)
        ViewGroup layout_news;

        public regularViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mclicklistener != null) {
                        mclicklistener.onItemClick(mDataset.get(getPosition()), IMAGE_VIEW);
                    }
                }

            });
        }

        public void bindArticleData(Article article) {
            final regularViewHolder viewHolder = this;
            if (!article.getThumbNail().isEmpty()) {
                Glide.with(mContext).
                        load(article.getThumbNail())
                        .asBitmap()
                        .fitCenter()
                        .animate(R.anim.image_anim)
                        .placeholder(R.drawable.nytimes)
                        .error(R.drawable.nytimes)
                        .into(new SimpleTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                vIcon.setImageBitmap(resource);
                                setCellColors(resource, viewHolder);
                            }

                        });
            }
            articleTitle.setText(article.getHeadline());
        }
    }

    public class textViewHolder extends ViewHolder {
        @BindView(R.id.newsTitle)
        TextView articleTitle;
        @BindView(R.id.newsDetails)
        TextView articleDetails;

        public textViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mclicklistener != null) {
                        mclicklistener.onItemClick(mDataset.get(getPosition()), IMAGE_VIEW);
                    }
                }

            });
        }

        public void bindArticleData(Article article) {
            articleTitle.setText(article.getHeadline());
            articleDetails.setText(article.getParagraph());
        }
    }

    public void setCellColors(Bitmap b, final regularViewHolder viewHolder) {

        if (b != null) {
            Palette.generateAsync(b, new Palette.PaletteAsyncListener() {

                @Override
                public void onGenerated(Palette palette) {

                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                    if (vibrantSwatch != null) {

                        viewHolder.articleTitle.setTextColor(vibrantSwatch.getTitleTextColor());
                        animationUtils.animateViewColor(viewHolder.layout_news,
                                mContext.getResources().getColor(R.color.book_without_palette),
                                vibrantSwatch.getRgb());

                    } else {

                        Log.e("[ERROR]", "The VibrantSwatch were null at: ");
                    }
                }
            });
        }
    }

    //Interface to handle recylerview onClick Events
    public interface OnItemClickListener {
        public void onItemClick(Article contact, int type);
    }
}
