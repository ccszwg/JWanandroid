package pers.jay.wanandroid.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.utils.JUtils;
import pers.zjc.commonlibs.util.StringUtils;

public class ArticleAdapter extends BaseQuickAdapter<Article, BaseViewHolder> {

    public static final int TYPE_COMMON = 1;
    public static final int TYPE_COLLECTION = 2;

    private Animation likeAnimation;
    private int mType;
    private RequestOptions options = new RequestOptions()
                                                         .placeholder(JApplication.getInstance().getDrawable(R.color.gray))
                                                         .error(JApplication.getInstance().getDrawable(R.color.red))
                                                         .transform(new RoundedCorners(20));
    private LikeListener likeListener;

    public ArticleAdapter(List<Article> articles, int type) {
        super(R.layout.item_article, articles);
        mType = type;
        likeAnimation = AnimationUtils.loadAnimation(JApplication.getInstance(), R.anim.anim_fade_out);
    }

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Article item) {
        helper.setText(R.id.tvAuthor, StringUtils.isEmpty(item.getAuthor()) ? item.getShareUser() : StringUtils.isEmpty(item.getAuthor()) ? "匿名" : item.getAuthor())
              .setText(R.id.tvDate, item.getNiceDate())
              .setText(R.id.tvTitle, JUtils.html2String(item.getTitle()))
              .setText(R.id.tvDesc, JUtils.html2String(item.getDesc()))
              .setGone(R.id.tvTagTop, item.isTop())
              .setGone(R.id.tvTagNew, item.isFresh())
              .setGone(R.id.tvTagQa, StringUtils.equals(item.getSuperChapterName(), "问答"))
              .setGone(R.id.tvDesc, !StringUtils.isEmpty(item.getDesc()))
              .setGone(R.id.ivProject, !StringUtils.isEmpty(item.getEnvelopePic()));
        LikeButton ivLike = helper.itemView.findViewById(R.id.ivLike);
        ivLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeListener.liked(item, helper.getAdapterPosition());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeListener.unLiked(item, helper.getAdapterPosition());
            }
        });
        switch (mType) {
            case TYPE_COLLECTION:
                ivLike.setLiked(true);
                helper.setText(R.id.tvType,item.getChapterName());
                break;
            case TYPE_COMMON:
            default:
                ivLike.setLiked(item.isCollect());
                helper.setText(R.id.tvType, String.format("%s/%s", item.getSuperChapterName(), item.getChapterName()));
                break;
        }
        ImageView ivProject = helper.itemView.findViewById(R.id.ivProject);
        if (!StringUtils.isEmpty(item.getEnvelopePic())) {
            Glide.with(mContext).load(item.getEnvelopePic()).apply(options).into(ivProject);
        }
    }

    public interface LikeListener {
        void liked(Article item, int adapterPosition);
        void unLiked(Article item, int adapterPosition);
    }

    /* 效果是列表所有item都生效 */
    public void loadAnim(ImageView view, boolean collect) {
        if (collect) {
            likeAnimation = AnimationUtils.loadAnimation(JApplication.getInstance(), R.anim.anim_fade_in);
        }

        likeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (collect) {
                    Glide.with(mContext).load(R.drawable.ic_like_fill).into(view);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_like).into(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        view.startAnimation(likeAnimation);
    }

}
