/*
 * Copyright (c) 2016 - 2017 Rui Zhao <renyuneyun@gmail.com>
 *
 * This file is part of Easer.
 *
 * Easer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Easer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Easer.  If not, see <http://www.gnu.org/licenses/>.
 */

package ryey.easer.plugins.operation.media_control;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import ryey.easer.commons.plugindef.operationplugin.OperationData;
import ryey.easer.commons.plugindef.operationplugin.OperationLoader;

public class MediaControlLoader extends OperationLoader {
    public MediaControlLoader(Context context) {
        super(context);
    }

    @Override
    public boolean _load(OperationData data) {
        MediaControlOperationData.ControlChoice choice = (MediaControlOperationData.ControlChoice) data.get();
        switch (choice) {
            case play_pause:
                emitMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            case play:
                emitMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY);
                break;
            case pause:
                emitMediaButton(KeyEvent.KEYCODE_MEDIA_PAUSE);
                break;
            case previous:
                emitMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                break;
            case next:
                emitMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT);
                break;
            default:
                return false;
        }
        return true;
    }

    private void emitMediaButton(int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }
}
