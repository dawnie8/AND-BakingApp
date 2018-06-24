package ipomoea.bakingapp;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import ipomoea.bakingapp.model.Step;

public class DescriptionFragment extends Fragment {

    Step step;
    TextView descriptionTextView;
    ImageView stepImage;
    String videoUrl;
    String thumbnailUrl;
    Long position;
    SimpleExoPlayerView playerView;
    SimpleExoPlayer exoPlayer;
    boolean autoplay = true;

    public static DescriptionFragment newInstance(Step step) {
        DescriptionFragment descriptionFragment = new DescriptionFragment();

        Bundle args = new Bundle();
        args.putParcelable("stepIndex", step);
        descriptionFragment.setArguments(args);

        return descriptionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable("stepIndex");
            videoUrl = step.getVideoUrl();
            thumbnailUrl = step.getThumbnailUrl();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);
        descriptionTextView = rootView.findViewById(R.id.step_description_textView);
        stepImage = rootView.findViewById(R.id.step_thumbnail);
        playerView = rootView.findViewById(R.id.playerView);

        position = C.TIME_UNSET;
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong("position", C.TIME_UNSET);
            autoplay = savedInstanceState.getBoolean("autoplay", true);
        }

        if (step != null) {
            descriptionTextView.setText(step.getDescription());
            if (TextUtils.isEmpty(videoUrl)) {
                playerView.setVisibility(View.GONE);
            } else {
                playerView.setVisibility(View.VISIBLE);

                if (!RecipeDetailActivity.twoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LinearLayout layout = rootView.findViewById(R.id.description_linearLayout);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    layout.setPadding(0, 0, 0, 0);
                    descriptionTextView.setVisibility(View.GONE);
                    stepImage.setVisibility(View.GONE);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    View decorView = getActivity().getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }

            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getActivity())
                        .load(thumbnailUrl)
                        .into(stepImage);
                stepImage.setPadding(0,12,0,12);
            }

        }

        return rootView;
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            playerView.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);

            if (position != C.TIME_UNSET) {
                exoPlayer.seekTo(position);
            }
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(autoplay);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!videoUrl.equals(""))
            initializePlayer(Uri.parse(videoUrl));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            position = exoPlayer.getCurrentPosition();
            autoplay = exoPlayer.getPlayWhenReady();
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("position", position);
        outState.putBoolean("autoplay", autoplay);
    }

}