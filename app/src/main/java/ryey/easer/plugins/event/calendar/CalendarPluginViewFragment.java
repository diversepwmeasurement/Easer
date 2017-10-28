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

package ryey.easer.plugins.event.calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import ryey.easer.R;
import ryey.easer.Utils;
import ryey.easer.commons.plugindef.PluginViewFragment;
import ryey.easer.commons.plugindef.StorageData;

public class CalendarPluginViewFragment extends PluginViewFragment {

    final static String ACTION_RETURN = "ryey.easer.plugins.event.bluetooth_device.return_from_dialog";
    final static String EXTRA_CALENDAR_ID = "ryey.easer.plugins.event.calendar.extra.calendar_id";
    final static String EXTRA_CALENDAR_NAME = "ryey.easer.plugins.event.calendar.extra.calendar_name";

    long calendar_id = -1;
    TextView tv_calendar_name;
    CheckBox[] cb_conditions = new CheckBox[CalendarData.condition_name.length]; // The same order as `CalendarData.condition_name`

    IntentFilter mFilter = new IntentFilter(ACTION_RETURN);
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_RETURN)) {
                calendar_id = intent.getLongExtra(EXTRA_CALENDAR_ID, -1);
                tv_calendar_name.setText(intent.getStringExtra(EXTRA_CALENDAR_NAME));
            }
        }
    };

    {
        expectedDataClass = CalendarEventData.class;
        setDesc(R.string.event_calendar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getContext().registerReceiver(mReceiver, mFilter);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plugin_event__calendar, container, false);
        tv_calendar_name = (TextView) view.findViewById(R.id.text_calendar_name);
        cb_conditions[0] = (CheckBox) view.findViewById(R.id.checkBox_start);
        cb_conditions[1] = (CheckBox) view.findViewById(R.id.checkBox_end);

        view.findViewById(R.id.calendar_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.hasPermission(getContext(), Manifest.permission.READ_CALENDAR))
                    return;
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
                builderSingle.setTitle(R.string.ecalendar_select_dialog_title);
                final ArrayAdapter<CalendarWrapper> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice);
                for (CalendarWrapper wrapper : CalendarHelper.getCalendars(getContext().getContentResolver())) {
                    arrayAdapter.add(wrapper);
                }
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long id = arrayAdapter.getItem(which).id;
                        String name = arrayAdapter.getItem(which).name;
                        Intent intent = new Intent(ACTION_RETURN);
                        intent.putExtra(EXTRA_CALENDAR_ID, id);
                        intent.putExtra(EXTRA_CALENDAR_NAME, name);
                        getContext().sendBroadcast(intent);
                    }
                });
                builderSingle.show();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    protected void _fill(StorageData data) {
        if (data instanceof CalendarEventData) {
            CalendarData calendarData = (CalendarData) data.get();
            calendar_id = calendarData.calendar_id;
            tv_calendar_name.setText(CalendarHelper.getCalendarName(
                    getContext().getContentResolver(), calendar_id));
            for (int i = 0; i < CalendarData.condition_name.length; i++) {
                cb_conditions[i].setChecked(calendarData.conditions.get(CalendarData.condition_name[i]));
            }
        }
    }

    @Override
    public StorageData getData() {
        CalendarData calendarData = new CalendarData();
        calendarData.calendar_id = calendar_id;
        for (int i = 0; i < cb_conditions.length; i++) {
            calendarData.conditions.put(
                    CalendarData.condition_name[i], cb_conditions[i].isChecked());
        }
        return new CalendarEventData(calendarData);
    }

    static class CalendarWrapper {
        long id;
        String name;
        public String toString() {
            return name;
        }
    }

}