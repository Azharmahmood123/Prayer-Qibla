package noman.hijri.adapter;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.community.expandablerecycler.ExpandableRecyclerAdapter;
import noman.hijri.alarm_notifications.YearlyEventNotifications;
import noman.hijri.fragment.EventFragment;
import noman.hijri.helper.DateConverter;

public class EventAdapter extends ExpandableRecyclerAdapter<EventAdapter.PeopleListItem> {


    public static final int TYPE_PERSON = 1001;
    DateConverter dateConverter;
    Context context;
    int[] dateArray = {1, 10, 12, 1, 27, 1, 10, 11};
    int[] monthArray = {0, 0, 2, 8, 8, 9, 11, 11};
    EventFragment eventFragment;
    String[] hijriEventsArray;
    String[] monthNames;
    String[] dataArray = {"The Islamic New Year starts on the first day of Muharram. Muharram is one of the special holy months for Muslims which means \"sacred\". The first day of Muharram commemorates the migration of the Prophet Muhammad from Makkah to Madinah in 622, which is considered as year one in the Islamic Calendar. Muslims observe the New Year\'s day by going to the mosques to worship and listen to the stories of Prophet Muhammad (P.B.U.H.) and early Muslims.",
            "The Ashura simply means \"tenth\" in Arabic, which marks the climax of the remembrance of Muharram. It commemorates the martyrdom of Hussain Ibne Ali (R.A), the grandson of Prophet Muhammad (P.B.U.H.) at the battle of Karbala in 61 AH (680 AD), and is a day for mourning.",
            "According to the Islamic Calendar, 12th Rabi-ul-Awwal is the birthday of Prophet Muhammad (P.B.U.H.). He was born in the prestigious house of Quraish, the most revered family of Arabia. This house was entrusted with the upkeep of Kaaba; the house of God, at Makkah, first built by Adam and centuries later built by Abraham.",
            "Ramadan is considered to be the most holy and blessed month in Islamic Calendar. Muslims (except children, the sick ones and the elderly people) abstain from food, drink and certain other activities during daylight hours in this month. This is also considered as the holiest month in the Islamic year as it commemorates the time when Quran was revealed to the Prophet Muhammad (P.B.U.H.).",
            "Lailatul Qadr is a night in the month of Ramadan which means \"the night of power\". In this night (27th of Ramadan), the Holy Quran was revealed to the Prophet Muhammad (PBUH). The messenger of Allah (P.B.U.H.) told us to search for Lailatul Qadr in the odd numbered nights, from the last ten days of Ramadan. Usually it is celebrated on the 27th night of Ramdan. It is a night of great importance and enormous blessings of Allah for Muslims.",
            "The Eid-ul-Fitr is a joyous day as it\'s a real thanksgiving for the believing men and women. On this day, Muslims show their real joy for health, strength and the opportunities of life, which Allah has provided to fulfill their obligations of fasting and other good deeds during the month of Ramadan. On this day, every believing man, woman and child should go to the prayer ground and participate in this joyous occasion.",
            "Performing Hajj is at least once in the lifetime is a duty of every Muslim who can physically and financially able to go to Makkah. Financial ability is meant to ensure that a Muslim can take care of his family first.",
            "Eid-ul-Adha is celebrated in the last month of Islamic Calendar on 10th day. It is also known as the feast of sacrifice as it includes the sacrifice of an animal as a token of thanksgiving to Allah\'s mercy. This Eid is celebrated by Muslims to mark the occasion when Allah appeared to Prophet Abraham in his dream and asked him to sacrifice his son Ismaeel, to demonstrate his devotion to Allah. Abraham was about to sacrifice his son when Allah stopped him and gave him a lamb to slaughter.",
            "We have put our best efforts to verify the information contained in the Islamic Calendar application.\n\nHowever, if you find any incomplete/inaccurate information or technical failure please contact us at\ninfo@quranreading.com"};


    public EventAdapter(Context context, EventFragment eventFragment) {
        super(context);

        this.eventFragment = eventFragment;
        dateConverter = new DateConverter(context);
        this.context = context;
        monthNames = context.getResources().getStringArray(R.array.month_name);
        hijriEventsArray = context.getResources().getStringArray(R.array.event_name);
        setItems(getSampleItems());

    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String title, date, description;


        public PeopleListItem(String title, String date) {
            super(TYPE_HEADER);
            this.date = date;
            this.title = title;
        }

        public PeopleListItem(String description) {
            super(TYPE_PERSON);
            this.description = description;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name, date;
        ImageView img_calander, imgArrow;
        View viewEvent;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));
            viewEvent = view;
            imgArrow = (ImageView) view.findViewById(R.id.item_arrow);
            date = (TextView) view.findViewById(R.id.item_event_date);
            name = (TextView) view.findViewById(R.id.item_event_name);
            img_calander = (ImageView) view.findViewById(R.id.item_calender);
        }

        public void bind(final int position) {
            super.bind(position);
            date.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            name.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            date.setText(visibleItems.get(position).date);
            name.setText(visibleItems.get(position).title);

            img_calander.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    for (int i = 0; i < dateArray.length; i++) {
                        if (i == position) {
                            int maxDays;
                            String greDate = "", monthName = "";
                            int date = dateArray[i];
                            int month = monthArray[i];

                            int year = ((CommunityGlobalClass) context.getApplicationContext()).yearSelected;
                            if (date == 0) {
                                greDate = "--------";
                                Toast.makeText(mContext, "Cannot get the correct date.", Toast.LENGTH_SHORT).show();
                            } else {
                                // maxDays = dateConverter.getHijriMonthMaxDay(month, year);
                               /* HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(date, month, year, maxDays, true);
                                String curGregorianDay = String.valueOf(dateConverted.get("DAY"));
                                int curGregorianMonth = dateConverted.get("MONTH");
                                String curGregorianYear = String.valueOf(dateConverted.get("YEAR"));
                                monthName = monthNames[curGregorianMonth - 1];
                                greDate = curGregorianDay + " " + monthName + " " + curGregorianYear;
                                //Its working just switching the tab
                                CommunityGlobalClass.getInstance().dialogDate = dateConverted.get("DAY");
                                //  CommunityGlobalClass.mCalenderTabsActivity.moveToCalenderTab();
*/


                                //  CommunityGlobalClass.mCalenderFragment.setGridCellAdapterToDate((curGregorianMonth ), dateConverted.get("YEAR"), "");
                                CommunityGlobalClass.getInstance().dialogDate = date;
                                CommunityGlobalClass.getInstance().selectDateOutSide = date;
                                CommunityGlobalClass.mCalenderFragment.setGridCellAdapterToDate(month, year, "");
                                CommunityGlobalClass.getInstance().mEventActivity.onBackPressed();
                            }
                        }
                    }
                }
            });


            //Add Alarams When come on Event screen

            for (int i = 0; i < dateArray.length; i++) {
                if (i == position) {
                    int maxDays;
                    String greDate = "", monthName = "";
                    int date = dateArray[i];
                    int month = monthArray[i];

                    int year = ((CommunityGlobalClass) context.getApplicationContext()).yearSelected;
                    if (date == 0) {
                        greDate = "--------";
                        // Toast.makeText(mContext, "Cannot get the correct date.", Toast.LENGTH_SHORT).show();
                    } else {
                        int[] dates = dateConverter.ummalQuraCalendar(date, month, year);
                        // greDate = curGregorianDay + " " + monthName + " " + curGregorianYear;
                        if (year == CommunityGlobalClass.mCalenderFragment.currentYear && month >= CommunityGlobalClass.mCalenderFragment.currentMonth
                                ) {

                            setEventAlarm(date, month, dates[2], dates[0], dates[1]);
                        }
                    }
                }
            }
            // ********************** END **********
        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name, linkText;
        String[] link = mContext.getResources().getStringArray(R.array.islamic_month_detail_link);

        public PersonViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            linkText = (TextView) view.findViewById(R.id.txt_hyperLink);
        }

        public void bind(final int position) {
            name.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            name.setText(getGregorianDate((position - 1)) + "\n\n" + visibleItems.get(position).description);
            linkText.setClickable(true);
            linkText.setMovementMethod(LinkMovementMethod.getInstance());
            linkText.setText("For more information:\n" + link[position - 1]);
            linkText.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            //linkText.setText(""+link[position - 1]);

            if (linkText.getLinksClickable() == true) {
                linkText.setLinkTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.item_event_header, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.item_event_detail, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:

                ((HeaderViewHolder) holder).bind(position);


                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) holder).bind(position);
                break;
        }
    }

    private List<PeopleListItem> getSampleItems() {
        List<PeopleListItem> items = new ArrayList<>();
        for (int position = 0; position < dateArray.length; position++) {
            int maxDays;
            String greDate = "", monthName = "";
            int date = dateArray[position];
            int month = monthArray[position];

            int year = ((CommunityGlobalClass) context.getApplicationContext()).yearSelected;
            if (date == 0) {
                greDate = "     --------";
            } else {
                maxDays = dateConverter.getHijriMonthMaxDay(month, year);

                HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(date, month, year, maxDays, true);
                String curGregorianDay = String.valueOf(dateConverted.get("DAY"));
                int curGregorianMonth = dateConverted.get("MONTH");
                String curGregorianYear = String.valueOf(dateConverted.get("YEAR"));

                monthName = monthNames[curGregorianMonth - 1];
                greDate = curGregorianDay + " " + monthName + " " + curGregorianYear;
            }

            String name = hijriEventsArray[position];
            String description = dataArray[position];
            //Add name and Event Date in expandable-list

            //header
            items.add(new PeopleListItem(name, greDate));
            //descripton
            items.add(new PeopleListItem(description));

        }


        return items;
    }

   /* public String getGregorianDate(int i)
    {

                int maxDays;
                String greDate = "", monthName = "";
                int date = dateArray[i];
                int month = monthArray[i];

                int year = ((CommunityGlobalClass) context.getApplicationContext()).yearSelected;
                if (date == 0) {
                    greDate = "--------";
                    Toast.makeText(mContext,"Cannot get the correct date.",Toast.LENGTH_SHORT).show();
                } else {
                    maxDays = dateConverter.getHijriMonthMaxDay(month, year);
                    HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(date, month, year, maxDays, true);
                    String curGregorianDay = String.valueOf(dateConverted.get("DAY"));
                    int curGregorianMonth = dateConverted.get("MONTH");
                    String curGregorianYear = String.valueOf(dateConverted.get("YEAR"));
                    monthName = monthNames[curGregorianMonth - 1];
                    greDate = curGregorianDay + " " + monthName + " " + curGregorianYear;
                }

        return greDate;
    }*/

    public String getGregorianDate(int i) {

        int maxDays;
        String greDate = "", monthName = "";
        int date = dateArray[i];
        int month = monthArray[i];

        int year = ((CommunityGlobalClass) context.getApplicationContext()).yearSelected;
        if (date == 0) {
            greDate = "--------";
            Toast.makeText(mContext, "Cannot get the correct date.", Toast.LENGTH_SHORT).show();
        } else {
            maxDays = dateConverter.getHijriMonthMaxDay(month, year);
            HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(date, month, year, maxDays, true);
            String curGregorianDay = String.valueOf(dateConverted.get("DAY"));
            int curGregorianMonth = dateConverted.get("MONTH");
            String curGregorianYear = String.valueOf(dateConverted.get("YEAR"));
            monthName = monthNames[curGregorianMonth - 1];

            greDate = dateConverter.getGerorgerianDate(date, month, year);
            // greDate = curGregorianDay + " " + monthName + " " + curGregorianYear;
          /*  if(year == CommunityGlobalClass.mCalenderFragment.currentYear) {
                setEventAlarm(date , month,year);
            }*/

        }

        return greDate;
    }

    public void setEventAlarm(int date, int month, int geoYear, int geoDate, int geoMonth) {
        YearlyEventNotifications yearlyNotifications = new YearlyEventNotifications(mContext);
        yearlyNotifications.setDailyAlarm1(date, month, geoYear, geoDate, geoMonth);

    }
}
