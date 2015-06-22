package bandsintown.com.sandbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);

		final ImageView iv = (ImageView) findViewById(R.id.main_image);

		displayImageGlide(BaseActivity.IMAGE_URL, iv);

		Button button = (Button) findViewById(R.id.main_button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DifferentActivity.class);
				startActivity(intent, iv);
			}

		});
	}

}
