package bandsintown.com.sandbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.*;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

	protected Transition mSharedEnterTransition;
	protected Transition mSharedExitTransition;
	protected Transition mSharedReturnTransition;
	protected Transition mSharedReenterTransition;

	public static final String IMAGE_URL = "http://s3.amazonaws.com/bit-photos/large/2334700.jpeg";
	public static final String THUMB_URL = "http://s3.amazonaws.com/bit-photos/thumb/2334700.jpeg";
	public static final String CONTROL = "http://s3.amazonaws.com/bit-photos/large/226052.jpeg";
	public static final String THUMB_TRANSITION = "thumb_trans";

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			buildSharedElementTransitions();
	}

	public void startActivity(Intent intent, View... sharedElements) {
		startActivity(intent, true, null, sharedElements);
	}

	public void startActivity(Intent intent, boolean useDefaults, ActivityOptionsCompat animations, View... sharedElements) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			View statusBar = findViewById(android.R.id.statusBarBackground);
			View navigationBar = findViewById(android.R.id.navigationBarBackground);

			//TODO figure out how to better animate the action bar section. has to do with the blur bitmap

			ArrayList<Pair> pairs = new ArrayList<>();
			if(sharedElements != null && sharedElements.length > 0) {
				for(View view : sharedElements)
					pairs.add(Pair.create(view, view.getTransitionName()));
			}

			if(useDefaults) {
				if(statusBar != null)
					pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));

				if(navigationBar != null)
					pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
			}

			ActivityOptionsCompat options;
			if(pairs.size() > 0) {
				//noinspection unchecked
				options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs.toArray(new Pair[pairs.size()]));
			}
			else
				options = null;

			if(animations != null && options != null)
				options.update(animations);

			//TODO figure out how to remove views from this pair if needed

			ActivityCompat.startActivity(this, intent, options != null ? options.toBundle() : null);
		}
		else
			startActivity(intent);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void buildSharedElementTransitions() {
		TransitionSet enterTransition = new TransitionSet();
		enterTransition.addTransition(new ChangeBounds());
		enterTransition.addTransition(new ChangeClipBounds());
		enterTransition.addTransition(new ChangeImageTransform());
		enterTransition.addTransition(new ChangeTransform());

		mSharedEnterTransition = enterTransition;
		getWindow().setSharedElementEnterTransition(mSharedEnterTransition);

		TransitionSet exitTransition = new TransitionSet();
		exitTransition.addTransition(new ChangeBounds());
		exitTransition.addTransition(new ChangeClipBounds());
		exitTransition.addTransition(new ChangeImageTransform());
		exitTransition.addTransition(new ChangeTransform());

		mSharedExitTransition = exitTransition;
		getWindow().setSharedElementExitTransition(mSharedExitTransition);

		TransitionSet returnTransition = new TransitionSet();
		returnTransition.addTransition(new ChangeBounds());
		returnTransition.addTransition(new ChangeClipBounds());
		returnTransition.addTransition(new ChangeImageTransform());
		returnTransition.addTransition(new ChangeTransform());

		mSharedReturnTransition = returnTransition;
		getWindow().setSharedElementReturnTransition(mSharedReturnTransition);

		TransitionSet reenterTransition = new TransitionSet();
		reenterTransition.addTransition(new ChangeBounds());
		reenterTransition.addTransition(new ChangeClipBounds());
		reenterTransition.addTransition(new ChangeImageTransform());
		reenterTransition.addTransition(new ChangeTransform());

		mSharedReenterTransition = reenterTransition;
		getWindow().setSharedElementReenterTransition(mSharedReenterTransition);
	}

	public void displayImageGlide(String url, ImageView imageView) {
		Glide.with(this).load(url)
				.asBitmap().transform(new CustomTransformation(this))
				.skipMemoryCache(true)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into(imageView);
	}

	private static class CustomTransformation extends BitmapTransformation {

		public CustomTransformation(Context context) {
			super(context);
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
			return bitmapChanger(toTransform, outWidth, outHeight);
		}

		@Override
		public String getId() {
			return "some_id_1";
		}

	}

	private static Bitmap bitmapChanger(Bitmap bitmap, int desiredWidth, int desiredHeight) {
		float originalWidth = bitmap.getWidth();
		float originalHeight = bitmap.getHeight();

		float scaleX = desiredWidth / originalWidth;
		float scaleY = desiredHeight / originalHeight;

		//Use the larger of the two scales to maintain aspect ratio
		float scale = Math.max(scaleX, scaleY);

		Matrix matrix = new Matrix();

		matrix.setScale(scale, scale);

		//If the scaleY is greater, we need to center the image
		if(scaleX < scaleY) {
			float tx = (scale * originalWidth - desiredWidth) / 2f;
			matrix.postTranslate(-tx, 0f);
		}

		Bitmap result = Bitmap.createBitmap(desiredWidth, desiredHeight, bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bitmap, matrix, new Paint());
		return result;
	}

}
