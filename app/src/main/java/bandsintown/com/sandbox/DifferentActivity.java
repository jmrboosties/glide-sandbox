package bandsintown.com.sandbox;

import android.os.Bundle;
import android.widget.ImageView;

public class DifferentActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.different);

		ImageView imageView = (ImageView) findViewById(R.id.diff_image);

		displayImageGlide(BaseActivity.IMAGE_URL, imageView);
	}

}
