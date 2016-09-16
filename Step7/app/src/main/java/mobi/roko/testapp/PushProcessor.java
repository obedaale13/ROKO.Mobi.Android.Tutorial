package mobi.roko.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.rokolabs.sdk.RokoMobi;
import com.rokolabs.sdk.promo.ResponsePromo;
import com.rokolabs.sdk.promo.RokoPromo;
import com.rokolabs.sdk.promo.RokoPromoDeliveryType;
import com.rokolabs.sdk.push.PushData;
import com.rokolabs.sdk.push.RokoPush;

public class PushProcessor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_processor);
        final TextView pushMessageTextView = (TextView) findViewById(R.id.push_message);

        RokoMobi.start(this);
		RokoPush.notificationOpened(getIntent(), new RokoPush.CallbackNotificationOpened() {
			@Override
			public void success(final PushData data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (data.promoCode != null && !data.promoCode.isEmpty()) {
							RokoPromo.loadPromoDiscountWithPromoCode(data.promoCode, new RokoPromo.CallbackDiscountLoaded() {
								@Override
								public void success(ResponsePromo responsePromo) {
									SharedPreferences sharedPreferences = PushProcessor.this.getSharedPreferences("Promo", Context.MODE_PRIVATE);
									SharedPreferences.Editor editor = sharedPreferences.edit();
									editor.putString("code", data.promoCode);
									editor.putFloat("value", (float) responsePromo.data.value);
									editor.putInt("deliveryType", RokoPromoDeliveryType.PUSH.ordinal()).commit();
								}

								@Override
								public void failure(ResponsePromo responsePromo) {
								}
							});
						}
						pushMessageTextView.setText(data.message);
					}
				});
			}
		});
    }
}
