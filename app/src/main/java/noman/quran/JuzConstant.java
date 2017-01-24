package noman.quran;


import com.quranreading.qibladirection.R;

import noman.CommunityGlobalClass;

/**
 * Created by Administrator on 12/23/2016.
 */

public class JuzConstant  {

    public static int[] flag_images = {R.drawable.off_translation, R.drawable.flag_english, R.drawable.flag_english, R.drawable.flag_english, R.drawable.flag_english, R.drawable.flag_english, R.drawable.flag_english, R.drawable.flag_urdu, R.drawable.flag_spanish, R.drawable.flag_france, R.drawable.flag_chinese, R.drawable.flag_persian, R.drawable.flag_italian, R.drawable.flag_dutch, R.drawable.flag_indonesia, R.drawable.flag_melayu, R.drawable.flag_hindi, R.drawable.flag_bangla, R.drawable.flag_turkish};
    public static CharSequence[] translationList = {"Off", "English (Saheeh)", "English (Pickthal)", "English (Shakir)", "English (Maududi)", "English (Daryabadi)", "English (Yusuf Ali)", "Urdu", "Spanish", "French", "Chinese", "Persian", "Italian", "Dutch", "Indonesian", "Melayu", "Hindi", "Bangla", "Turkish"};
    public static int fontSize_English[] = {(int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._4sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._6sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._8sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._18sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._20sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._24sdp)};
    public static int fontSize_Arabic[] = {(int)CommunityGlobalClass.getInstance(). getResources().getDimension(R.dimen._6sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._10sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._14sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._18sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._24sdp), (int) CommunityGlobalClass.getInstance().getResources().getDimension(R.dimen._28sdp)};




    /* Android
     30 Juz Tags
     {Surah #, Ayah #, Juz #}*/
    public static String[] arabicCounting = {"٥","١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "١٠", "١١", "١٢", "١٣", "١٤", "١٥", "١٦", "١٧", "١٨", "١٩", "٢٠", "٢١", "٢٢", "٢٣", "٢٤", "٢٥", "٢٦", "٢٧", "٢٨", "٢٩", "٣٠", "٣١", "٣٢", "٣٣", "٣٤", "٣٥", "٣٦", "٣٧", "٣٨", "٣٩", "٤٠", "٤١", "٤٢", "٤٣", "٤٤", "٤٥", "٤٦", "٤٧", "٤٨", "٤٩", "٥٠", "٥١", "٥٢", "٥٣", "٥٤", "٥٥", "٥٦", "٥٧", "٥٨", "٥٩", "٦٠", "٦١", "٦٢", "٦٣", "٦٤", "٦٥", "٦٦", "٦٧", "٦٨", "٦٩", "٧٠", "٧١", "٧٢", "٧٣", "٧٤", "٧٥", "٧٦", "٧٧", "٧٨", "٧٩", "٨٠", "٨١", "٨٢", "٨٣", "٨٤", "٨٥", "٨٦", "٨٧", "٨٨", "٨٩", "٩٠",
            "٩١", "٩٢", "٩٣", "٩٤", "٩٥", "٩٦", "٩٧", "٩٨", "٩٩", "١٠٠", "١٠١", "١٠٢", "١٠٣", "١٠٤", "١٠٥", "١٠٦", "١٠٧", "١٠٨", "١٠٩", "١١٠", "١١١", "١١٢", "١١٣", "١١٤"
            , "١١٥", "١١٦", "١١٧", "١١٨", "١١٩", "١٢٠", "١٢١", "١٢٢", "١٢٣", "١٢٤", "١٢٥", "١٢٦", "١٢٧", "١٢٨", "١٢٩", "١٣٠", "١٣١", "١٣٢", "١٣٣", "١٣٤", "١٣٥",
            "١٣٦", "١٣٧", "١٣٨", "١٣٩", "١٤٠", "١٤١", "١٤٢", "١٤٣", "١٤٤", "١٤٥", "١٤٦", "١٤٧", "١٤٨", "١٤٩", "١٥٠", "١٥١", "١٥٢", "١٥٣", "١٥٤", "١٥٥", "١٥٦",
            "١٥٧", "١٥٨", "١٥٩", "١٦٠", "١٦١", "١٦٢", "١٦٣", "١٦٤", "١٦٥", "١٦٦", "١٦٧", "١٦٨", "١٦٩", "١٧٠", "١٧١", "١٧٢", "١٧٣", "١٧٤", "١٧٥", "١٧٦", "١٧٧",
            "١٧٨", "١٧٩", "١٨٠", "١٨١", "١٨٢", "١٨٣", "١٨٤", "١٨٥", "١٨٦", "١٨٧", "١٨٨", "١٨٩", "١٩٠", "١٩١", "١٩٢", "١٩٣", "١٩٤", "١٩٥", "١٩٦", "١٩٧", "١٩٨",
            "١٩٩", "٢٠٠", "٢٠١", "٢٠٢", "٢٠٣", "٢٠٤", "٢٠٥", "٢٠٦", "٢٠٧", "٢٠٨", "٢٠٩", "٢١٠", "٢١١", "٢١٢", "٢١٣", "٢١٤", "٢١٥", "٢١٦", "٢١٧", "٢١٨", "٢١٩",
            "٢٢٠", "٢٢١", "٢٢٢", "٢٢٣", "٢٢٤", "٢٢٥", "٢٢٦", "٢٢٧", "٢٢٨", "٢٢٩", "٢٣٠", "٢٣١", "٢٣٢", "٢٣٣", "٢٣٤", "٢٣٥", "٢٣٦", "٢٣٧", "٢٣٨", "٢٣٩", "٢٤٠",
            "٢٤١", "٢٤٢", "٢٤٣", "٢٤٤", "٢٤٥", "٢٤٦", "٢٤٧", "٢٤٨", "٢٤٩", "٢٥٠", "٢٥١", "٢٥٢", "٢٥٣", "٢٥٤", "٢٥٥", "٢٥٦", "٢٥٧", "٢٥٨", "٢٥٩", "٢٦٠", "٢٦١",
            "٢٦٢", "٢٦٣", "٢٦٤", "٢٦٥", "٢٦٦", "٢٦٧", "٢٦٨", "٢٦٩", "٢٧٠", "٢٧١", "٢٧٢", "٢٧٣", "٢٧٤", "٢٧٥", "٢٧٦", "٢٧٧", "٢٧٨", "٢٧٩", "٢٨٠", "٢٨١", "٢٨٢", "٢٨٣", "٢٨٤", "٢٨٥", "٢٨٦"};


    public static int[][] juzzIndex = {{1, 0, 1}, {2, 142, 2}, {2, 253, 3}, {3, 92, 4}, {4, 24, 5}, {4, 148, 6}, {5, 83, 7}, {6, 111, 8},
            {7, 88, 9}, {8, 41, 10}, {9, 94, 11}, {11, 6, 12}, {12, 53, 13}, {15, 2, 14}, {17, 0, 15}, {18, 75, 16}, {21, 0, 17}, {23, 0, 18}, {25, 21, 19}, {27, 60, 20},
            {29, 45, 21}, {33, 31, 22}, {36, 22, 23}, {39, 32, 24}, {41, 47, 25}, {46, 0, 26}, {51, 31, 27}, {58, 0, 28}, {67, 0, 29},
            {78, 0, 30}};


    /*  public static String[] juzStrings = {"1,1", "2,142", "2,253", "3,92", "4,24", "4,148", "5,83", "6,111", "7,88", "8,41", "9,94", "11,6", "12,53", "15,2", "17,1", "18,75", "21,1", "23,1", "25,21", "27,60",
              "29,45", "33,31", "36,22", "39,32", "41,47", "46,1", "51,31", "58,1", "67,1", "78,1"};
  */
    public static int[] juzSurrahNumber = {1, 2, 2, 3, 4, 4, 5, 6, 7, 8, 9, 11, 12, 15, 17, 18, 21, 23, 25, 27, 29, 33, 36, 39, 41, 46, 51, 58, 67, 78};
    public static int[] juzAyahNumber = {0, 142, 253, 92, 24, 148, 83, 111, 88, 41, 94, 6, 53, 2, 0, 75, 0, 0, 21, 60, 45, 31, 22, 32, 47, 0, 31, 0, 0, 0};
    public static String[] paraWithSurrahIndex = {"1", "1,2,3", "3,4", "4,5,6", "6,7", "7,8", "8,9", "9,10", "10,11", "11",
            "11,12",
            "12,13",
            "13",
            "13 ",
            "13,14",
            "14",
            "15",
            "15,16",
            "16",
            "16",
            "17",
            "17",
            "18",
            "18",
            "18,19",
            "19",
            "19,20",
            "20",
            "20,21",
            "21",
            "21",
            "21",
            "21,22",
            "22",
            "22",
            "22,23",
            "23",
            "23",
            "23,24",
            "24",
            "24,25",
            "25",
            "25",
            "25",
            "25",
            "26",
            "26",
            "26",
            "26",
            "26",
            "26,27",
            "27",
            "27",
            "27",
            "27",
            "27",
            "27",
            "28",
            "28",
            "28",
            "28",
            "28",
            "28",
            "28",
            "28",
            "28",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "29",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30",
            "30"};
}
