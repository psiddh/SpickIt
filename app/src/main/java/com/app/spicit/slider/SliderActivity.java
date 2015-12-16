package com.app.spicit.slider;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.spicit.R;
import com.github.glomadrian.loadingballs.BallView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SliderActivity extends FragmentActivity {
	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private static final int NUM_PAGES = 5;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	private ArrayList<Parcelable> mImageParcelableURI;

	private ActionBar mActBar = null;
	private BallView ballsView;

	private int currentPosition = -1;

	private class SliderAdapter extends PagerAdapter {

		Context mContext;
		LayoutInflater mLayoutInflater;

		public SliderAdapter(Context context) {
			mContext = context;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mImageParcelableURI.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public Object instantiateItem(final ViewGroup container, int position) {
			final View itemView = mLayoutInflater.inflate(R.layout.slider_item, container, false);

			//final LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.layout_slider, null);
			final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

			LoadImageTask task = new LoadImageTask(imageView, container, itemView, position);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			//task.execute();


			/*Uri uri = (Uri) mImageParcelableURI.get(position);
			Bitmap bmp = decodeSampledBitmapFromResource(uri.getPath(), 300, 300);

			if (bmp == null) {
				bmp = BitmapFactory.decodeResource(container.getResources(),
						R.drawable.empty_photo);
			}
			imageView.setImageBitmap(bmp);
			container.addView(itemView);*/

			return itemView;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((LinearLayout) object);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slider);

		mImageParcelableURI = getIntent().getParcelableArrayListExtra("uriList");

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new SliderAdapter(this);
		mPager.setAdapter(mPagerAdapter);
		//mPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

		ballsView = (BallView) findViewById(R.id.balls);
		//mPager.setPageTransformer(false, new ZoomOutPageTransformer());
		//mPager.setBackgroundResource(R.drawable.empty_photo);
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
				invalidateOptionsMenu();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (ViewPager.SCROLL_STATE_DRAGGING == state || ViewPager.SCROLL_STATE_SETTLING == state) {
					ballsView.setVisibility(View.VISIBLE);
					// TBD: Is this needed ? I guess no
					//mPager.requestDisallowInterceptTouchEvent(false);
				} else {
					ballsView.setVisibility(View.GONE);
					//mPager.requestDisallowInterceptTouchEvent(true);

				}
			}
		});

		mPager.setOffscreenPageLimit(2);

		mActBar = getActionBar();
		mActBar.setHomeButtonEnabled(true);
		mActBar.setDisplayHomeAsUpEnabled(true);
		mActBar.setDisplayShowTitleEnabled(false);

	}

	@Override
	public void onBackPressed() {
		// If the user is currently looking at the first step, allow the system to handle the
		// Back button. This calls finish() on this activity and pops the back stack.
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.menu_back:
				backMenuPressed();
				invalidateOptionsMenu();
				break;
			case R.id.menu_forward:
				nextMenuPressed();
				invalidateOptionsMenu();
				break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_slider, menu);

		int totalCount = mPager.getAdapter().getCount();

		MenuItem title = menu.findItem(R.id.title);
		MenuItem back = menu.findItem(R.id.menu_back);
		MenuItem forward = menu.findItem(R.id.menu_forward);

		title.setTitle("SlideShow " + ((currentPosition == -1) ? 1 : currentPosition + 1) + "/" + totalCount);
		if (currentPosition + 1  == totalCount) {
			forward.setEnabled(false);
		} else {
			//back.setEnabled(true);
			forward.setEnabled(true);
		}


		return true;
	}

	private void updateMenuItemTitle() {

	}

	private void backMenuPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	private void nextMenuPressed() {
		if (mPager.getCurrentItem() == mPager.getAdapter().getCount() ) {

		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
		}
	}

	private static Bitmap decodeSampledBitmapFromResource(String strPath,int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strPath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options,reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(strPath, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private final WeakReference<ViewGroup> viewGroupReference;
		private final WeakReference<View> itemViewReference;
		//private final WeakReference<ProgressBar> spinnerReference;


		private int position;

		public LoadImageTask(ImageView imageView, ViewGroup container, View itemView, int pos) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			viewGroupReference = new WeakReference<ViewGroup>(container);
			itemViewReference = new WeakReference<View>(itemView);
			//spinnerReference = new WeakReference<ProgressBar>(spinner);
			position = pos;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Uri uri = (Uri) mImageParcelableURI.get(position);
			return decodeSampledBitmapFromResource(uri.getPath(), 300, 300);
		}

		@Override
		protected void onPreExecute() {
			ImageView imageView = imageViewReference.get();
			int i = imageView.getVisibility();
			/*ProgressBar spinner = spinnerReference.get();
			if (spinner == null)
				return;
			spinner.setVisibility(View.VISIBLE);*/
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {

			/*ProgressBar spinner = spinnerReference.get();
			if (spinner != null) {
				spinner.setVisibility(View.GONE);
			}*/
			ImageView imageView = imageViewReference.get();
			ViewGroup container = viewGroupReference.get();
			View itemView = itemViewReference.get();

			if (imageView == null || container == null || itemView == null)
				return;
			//ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
			imageView.setImageBitmap(bmp);
			container.addView(itemView, 0);
		}
	}

	private boolean hasImage(@NonNull ImageView view) {
		Drawable drawable = view.getDrawable();
		boolean hasImage = (drawable != null);

		if (hasImage && (drawable instanceof BitmapDrawable)) {
			hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
		}

		return hasImage;
	}

	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

			/*if (!hasImage(imageView))  {
				return;
			}*/
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);
			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA +
						(scaleFactor - MIN_SCALE) /
								(1 - MIN_SCALE) * (1 - MIN_ALPHA));



			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}