package develop.admin.it.formular;

import android.content.Intent;
import android.os.IBinder;

/**
 * Created by IT-PC on 12/3/2017.
 */

interface IB {
    IBinder onBind(Intent arg0);

    int onStartCommand(Intent intent, int flags, int startId);
}
