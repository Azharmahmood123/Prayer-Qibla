package noman.hijri.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.hijri.fragment.CalenderFragment;
import noman.hijri.helper.DateConverter;
import noman.hijri.model.DateAccessModel;

public class CalendarAdapter extends BaseAdapter {
    private final Context _context;
    private final List<DateAccessModel> list;
    DateConverter dateConverter;
    List<LinearLayout> listLinearLayout = new ArrayList<>();
    CalenderFragment calenderFragment;
    boolean isDialogDateAlreadySelected = false;

    public CalendarAdapter(Context context, List<DateAccessModel> list, CalenderFragment calenderFragment) {
        //super();
        this._context = context;
        this.list = list;
        dateConverter = new DateConverter(context);
        this.calenderFragment = calenderFragment;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /* private view holder class */
    private class ViewHolder {
        TextView georgianDate;
        ImageView img_event;
        TextView hijriDate;
        LinearLayout dataCell;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String hijriDay = "";
        int selected = ((CommunityGlobalClass) _context.getApplicationContext()).selected;

       /* if (list.get(position).status.equals("current") || list.get(position).status.equals("within")) {
            HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(Integer.parseInt(list.get(position).date),
                    list.get(position).monthNo, Integer.parseInt(list.get(position).year), true);

            hijriDay = String.valueOf(dateConverted.get("DAY"));
        }*/

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.calendar_item, null);


            holder = new ViewHolder();

            holder.georgianDate = (TextView) convertView.findViewById(R.id.tv_georgian_date);
            holder.hijriDate = (TextView) convertView.findViewById(R.id.tv_hijri_date);
            holder.dataCell = (LinearLayout) convertView.findViewById(R.id.data_cell);
            holder.img_event = (ImageView) convertView.findViewById(R.id.img_event);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.dataCell.setBackgroundColor(Color.TRANSPARENT);
        holder.img_event.setVisibility(View.GONE);
        holder.georgianDate.setTypeface(((GlobalClass) _context.getApplicationContext()).faceRobotoR);
        holder.hijriDate.setTypeface(((GlobalClass) _context.getApplicationContext()).faceRobotoR);

        //Convert Hijri to Georgian
        int[] date = dateConverter.ummalQuraCalendar(Integer.parseInt(list.get(position).date),
                list.get(position).monthNo, Integer.parseInt(list.get(position).year));

        if (list.get(position).status.equals("notWithin")) {
         /*  holder.georgianDate.setVisibility(View.GONE);
            holder.hijriDate.setVisibility(View.GONE);*/
            holder.hijriDate.setTextColor(_context.getResources().getColor(R.color.color_gray));
            holder.hijriDate.setText(list.get(position).date);

            holder.georgianDate.setTextColor(_context.getResources().getColor(R.color.color_gray));
            holder.georgianDate.setText("" + date[0]);

        } else {
            holder.hijriDate.setVisibility(View.VISIBLE);
            holder.georgianDate.setVisibility(View.VISIBLE);

            holder.hijriDate.setText(list.get(position).date);
            holder.hijriDate.setTextColor(_context.getResources().getColor(R.color.colorPrimary));

            holder.georgianDate.setTextColor(Color.BLACK);
            holder.georgianDate.setText("" + date[0]);


            ////// Current Selected //////
            if (position == selected && !list.get(position).status.equals("current") && list.get(position).eventIndex == -1) {
                holder.dataCell.setBackgroundResource(R.drawable.selected_sqr);
                holder.georgianDate.setTextColor(Color.WHITE);
                //Add all list here to remove background form the select dialog date
                listLinearLayout.add(holder.dataCell);

                if (((CommunityGlobalClass) _context.getApplicationContext()).dialogDate > -1) {
                    //If select date from convert it show starting date gerorgian white color
                    holder.georgianDate.setTextColor(Color.BLACK);
                }

            }

            //For those opereation dates which are selected by converter or grey dates
            if (((CommunityGlobalClass) _context.getApplicationContext()).dialogDate > -1) {
                for (int i = 0; i < listLinearLayout.size(); i++) {
                    listLinearLayout.get(i).setBackgroundColor(Color.TRANSPARENT);
                }
                //Now select the selected dialog date
                if (list.get(position).date.equals(String.valueOf(((CommunityGlobalClass) _context.getApplicationContext()).dialogDate))) {
                    holder.georgianDate.setTextColor(Color.WHITE);
                    holder.dataCell.setBackgroundResource(R.drawable.selected_sqr);
                    ((CommunityGlobalClass) _context.getApplicationContext()).dialogDate = -1;
                    calenderFragment.gregorianToHijri(Integer.valueOf(list.get(position).date), list.get(position).monthNo, Integer.valueOf(list.get(position).year));
                    //Add here true because it will also applay color of today borday that make gero date white
                    if (list.get(position).status.equals("current")) {
                        isDialogDateAlreadySelected = true;
                    } else {
                        isDialogDateAlreadySelected = false;
                    }
                }
            }
            ////// Current Selected + Current Date + Event //////
            if (position == selected && list.get(position).status.equals("current") && list.get(position).eventIndex != -1) {
                //holder.dataCell.setBackgroundResource(R.drawable.event_today_selected);

                holder.dataCell.setBackgroundResource(R.drawable.event_selected);
                holder.img_event.setVisibility(View.VISIBLE);
                holder.georgianDate.setTextColor(Color.WHITE);

                //Auto detect today is Event Islamic
                CommunityGlobalClass.getInstance().isTodayEvent = true;
                CommunityGlobalClass.getInstance().todayEventPostion = list.get(position).eventIndex;
                //*****************
            }

            ////// Current Selected + Current Date //////
            else if (position == selected && list.get(position).status.equals("current") && !CommunityGlobalClass.mCalenderFragment.isOutSideGridCall) {
                holder.dataCell.setBackgroundResource(R.drawable.today_selected);
                holder.georgianDate.setTextColor(Color.WHITE);

            }

            ////// Current Selected + Event //////
            else if (position == selected && list.get(position).eventIndex != -1 && !CommunityGlobalClass.mCalenderFragment.isOutSideGridCall) {
                holder.dataCell.setBackgroundResource(R.drawable.event_selected);
                holder.img_event.setVisibility(View.VISIBLE);
                holder.georgianDate.setTextColor(Color.WHITE);


            }

            ////// Current Date //////
            else if (list.get(position).status.equals("current")) {
                if (!isDialogDateAlreadySelected)
                    holder.dataCell.setBackgroundResource(R.drawable.today_square);
            }

            ////// Event //////
            else if (list.get(position).eventIndex != -1) {
                //holder.dataCell.setBackgroundResource(R.drawable.event_circle);
                holder.img_event.setVisibility(View.VISIBLE);

            }


            ////// Not Current Selected + Current Date + Event //////
            if (position != selected && list.get(position).status.equals("current") && list.get(position).eventIndex != -1) {
                holder.dataCell.setBackgroundResource(R.drawable.today_event_border);
                holder.img_event.setVisibility(View.VISIBLE);
                //holder.georgianDate.setTextColor(Color.WHITE);
            }


        }


        return convertView;
    }


}
