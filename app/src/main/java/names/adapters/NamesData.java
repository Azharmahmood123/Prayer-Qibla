package names.adapters;

import android.content.Context;

import com.quranreading.qibladirection.R;

import java.util.ArrayList;

/**
 * Created by cyber on 1/3/2017.
 */

public class NamesData {

    private Context mContext;
    private String[] namesArabic;

    public NamesData(Context context) {
        mContext = context;
        namesArabic = mContext.getResources().getStringArray(R.array.allah_names);
    }

    private ArrayList<Integer> nameImageArray = new ArrayList<>();

    private String[] names = new String[]{"Allah", "Ar-Rahman", "Ar-Rahim", "Al-Malik", "Al-Quddus", "As-Salam", "Al-Mu'min", "Al-Muhaymin", "Al-Aziz", "Al-Jabbar", "Al-Mutakabbir", // 10
            "Al-Khaliq", "Al-Bari", "Al-Musawwir", "Al-Ghaffar", "Al-Qahhar", "Al-Wahhab", "Ar-Razzaq", "Al-Fattah", "Al-Aleem", "Al-Qabid", // 20
            "Al-Basit", "Al-Khafid", "Ar-Rafi", "Al-Mu'iz", "Al-Muzil", "As-Sami", "Al-Basir", "Al-Hakam", "Al-Adl", "Al-Latif", "Al-Khabir", "Al-Halim", "Al-Azim", "Al-Ghafoor", "Ash-Shakur", "Al-Ali", "Al-Kabir", "Al-Hafiz", "Al-Muqit", "Al-Hasib", "Aj-Jalil", "Al-Karim", "Ar-Raqib", "Al-Mujib",
            "Al-Wasi", "Al-Hakim", "Al-Wadud", "Al-Majid", "Al-Ba'ith", "Ash-Shahid", // 50
            "Al-Haqq", "Al-Wakil", "Al-Qawee", "Al-Matin", "Al-Walee", "Al-Hamid", "Al-Muhsi", "Al-Mubdi", "Al-Mu'eed", "Al-Muhyee", "Al-Mumeet", "Al-Hayy", "Al-Qayyum", "Al-Wajid", "Al-Majid", "Al-Wahid", "As-Samad", "Al-Qadir", "Al-Muqtadir", "Al-Muqaddim", // 70 67 "Al-Ahad "
            "Al-Mu'akhkhir", "Al-Awwal", "Al-Akhir", "Az-Zahir", "Al-Batin", "Al-Wali", "Al-Muta'ali", "Al-Barr", "At-Tawwab", "Al-Muntaqim", "Al-Afuww", "Ar-Ra'uf", "Malik Al-Mulk", "Zul-l-Jalal wal-Ikram", "Al-Muqsit", "Aj-Jami", "Al-Ghanee", "Al-Mughnee", "Al-Mani", "Ad-Darr", // 90
            "An-Nafi", "An-Nur", "Al-Hadi", "Al-Badi", "Al-Baqi", "Al-Warith", "Ar-Rashid", "As-Sabur"};

    private String[] namesMeaning = new String[]{"Name of the Lord", "The Most Merciful", "The Most Compassionate", "The King, The Monarch", "The Holy One", "The Peace, The Tranquility", "The Faithful, The Trusted", "The Vigilant, The Controller", "The Almighty, The Powerful", "The all Compelling",
            "The Imperious", "The Creator, The Maker", "The Artificer, The Creator", "The Organizer, The Designer", // 15
            "The Forgiving, The Forgiver", "The Dominant", "The Donor, The Bestower", "The Provider, The Sustainer", "The Opener, The Revealer", "The all Knowing", "The Restrainer, The Recipient", "The Expander", "The Abaser, The Humbler", "The Raiser, The Exalter", "The Honorer, The Exalter",
            "The Abaser, the Degrader", "The Hearer,The all Hearing & Knowing",

            "The Seer, The Discerning", "The Arbitrator, The Judge", "The equitable, The Just", "The Most Gentle, The Gracious", "The Aware, The Sagacious", "The Gentle, The Clement", "The Great, The Mighty", "The Forgiving, The Pardoner", "The Grateful, The Thankful", "The Most High, The Exalted",
            "The Great", "The Guardian, The Preserver", "The Maintainer, The Nourisher", "The Noble, The Reckoner",

            "The Majestic", "The Bountiful", "The Guardian", "The Responder", "The Knowledgeable", "The Judicious", "The Affectionate", "The Glorious", "The Resurrector", "The Witness", "The Truth", "The Trustee", "The Almighty, The Strong", "The Strong, The Firm", "The Friend",
            "The Praiseworthy , The Commendable", "The Counter", "The Creator, The Originator", "The Restorer", "The Bestower",

            "The Bringer of Death", "The Ever Living", "The Self-Subsistent, The Eternal", "The Opulent, The Finder", "The Noble, The Illustrious", "The One, The Unique", "The Perfect", "The Able, The Capable", "The all Powerful", "The Presenter, The Advancer", "The Fulfiller, The Keeper Behind",

            "The First", "The Last", "The Exterior, The Manifest", "The Hidden, the Interior", "The Ruler, The Master", "The Exalted and Most High", "The Benefactor, The Beneficent", "The Acceptor of Repentance, The Forgiver", "The Avenger", "The Effacer, The Pardoner",
            "The Merciful, The Ever Indulgent", "The Ruler of the Kingdom, The King of the Universe", "Lord of Majesty and Generosity", "The Just, The Equitable", "The Collector, The Comprehensive", "The Rich, The All Sufficing", "The Enricher, The Bestower", "The Prohibiter, The Defender",
            "The Distresser , The Afflictor", "The Beneficial Benefactor", "The Light", "The Guide", "The Wonderful, The Maker, The Incomparable", "The Enduring, The Everlasting", "The Inheritor, The Heir", "The Rightly Guided, The Guide", "The Most Patient, The Enduring"};

    private String[] namesDetailMeaning = new String[]{"Allah is the Name of the Lord, the Exalted. It is said that Allah is the Greatest Name of Allah, because it is referred to when describing Allah by the various attributes.",
            "The One who has plenty of mercy for the believers and the blasphemers in this world and especially for the believers in the hereafter", "The One who has plenty of mercy for the believers", "The One with the complete Dominion, the One Whose Dominion is clear from imperfection",
            "The One who is pure from any imperfection and clear from children and adversaries", "The One who is free from every imperfection",
            "The One who witnessed for Himself that no one is God but Him. And He witnessed for His believers that they are truthful in their belief that no one is God but Him", "The One who witnesses the saying and deeds of His creatures", "The Defeater who is not defeated",
            "The One that nothing happens in His Dominion except that which He willed", "The One who is clear from the attributes of the creatures and from resembling them", "The One who brings everything from non-existence to existence", "The Creator who has the Power to turn the entities",
            "The One who forms His creatures in different pictures", "The One who forgives the sins of His slaves time and time again", "The Subduer who has the perfect Power and is not unable over anything", "The One who is Generous in giving plenty without any return", "unknown for Ar-Razzaq",
            "The One who opens for His slaves the closed worldly and religious matters", "The Knowledgeable; The One nothing is absent from His knowledge", "The One who constricts the sustenance", "The One who expands and widens", "The One who lowers whoever He willed by His Destruction",
            "The One who raises whoever He willed by His Endowment", "He gives esteem to whoever He willed, hence there is no one to degrade Him", "Degrades whoever he willed, hence there is no one to give him esteem",
            "The One who Hears all things that are heard by His Eternal Hearing without an ear, instrument or organ", "The One who Sees all things that are seen by His Eternal Seeing without a pupil or any other instrument", "He is the Ruler and His judgment is His Word",
            "The One who is entitled to do what He does", "The One who is kind", "The One who knows the truth of things", "The One who delays the punishment for those who deserve it and then He might forgive them",
            "The One deserving the attributes of Exaltment, Glory, Extolment, and Purity from all imperfection", "The One who forgives a lot", "The One who gives a lot of reward for a little obedience", "The One who is clear from the attributes of the creatures",
            "The One who is greater than everything in status", "The One who protects whatever and whoever He willed to protect", "The One who has the Power", "The One who gives the satisfaction", "The One who is attributed with greatness of Power and Glory of status",
            "The One who is clear from abjectness", "The One that nothing is absent from Him. Hence it's meaning is related to the attribute of Knowledge", "The One who answers the one in need if he asks Him and rescues the yearner if he calls upon Him", "unknown for Al-Wasi",
            "The One who is correct in His doings", "unknown for Al-Wadud", "The One who is with perfect Power, High Status, Compassion, Generosity and Kindness", "The One who resurrects for reward and/or punishment", "The One who nothing is absent from Him", "The One who truly exists",
            "The One who gives the satisfaction and is relied upon", "The One with the complete Power", "The One with extreme Power which is un-interrupted and He does not get tired", "The One who supports & defends", "The praised One who deserves to be praised",
            "The One who the count of things are known to him", "The One who started the human being. That is, He created him", "The One who brings back the creatures after death",
            "The One who took out a living human from semen that does not have a soul. He gives life by giving the souls back to the worn out bodies on the resurrection day and He makes the hearts alive by the light of knowledge", "The One who renders the living dead",
            "The One attributed with a life that is unlike our life and is not that of a combination of soul, flesh or blood", "The One who remains and does not end", "The Rich who is never poor. Al-Wajid is Richness", "The One who is Noble", "The One without a partner",
            "The Master who is relied upon in matters and reverted to in ones needs", "The One attributed with Power", "The One with the perfect Power that nothing is withheld from Him", "He makes ahead what He wills", "The One who delays what He wills",
            "The One whose Existence is without a begining", "The One whose Existence is without an end", "The One that nothing is above Him and nothing is underneath Him, hence He exists without a place", "The One who is hidden", "The One who owns things and manages them",
            "The One who is clear from the attributes of the creation", "The One who is kind to His creatures, who covered them with His sustenance and specified whoever He willed among them by His support, protection, and special mercy",
            "The One who grants repentance to whoever He willed among His creatures and accepts his repentance", "The One who victoriously prevails over His enemies and punishes them for their sins. It may mean the One who destroys them", "The One with wide forgiveness",
            "The One with extreme Mercy. The Mercy of Allah is His will to endow upon whoever He willed among His creatures", "The One who controls the Dominion and gives dominion to whoever He willed", "The One who deserves to be Exalted and not denied", "The One who is Just in His judgment",
            "The One who gathers the creatures on a day that there is no doubt about, that is the Day of Judgment", "The One who does not need the creation", "The One who satisfies the necessities of the creatures", "The Supporter who protects and gives victory to His pious believers",
            "The One who makes harm reach to whoever He willed", "The One who gives benefits to whoever He wills", "The One who guides",
            "The One whom with His Guidance His believers were guided, and with His Guidance the living beings have been guided to what is beneficial for them and protected from what is harmful to them", "The One who created the creation and formed it without any preceding example",
            "The One that the state of non-existence is impossible for Him", "The One whose Existence remains", "The One who guides", "The One who does not quickly punish the sinners"};

    public void setNamesImage() {

        for (int i = 1; i < 100; i++) {
            String imageName = "name_" + i;
            Integer imgId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
            nameImageArray.add(imgId);
        }
    }

    public int getImageId(int position) {
        return nameImageArray.get(position);
    }

    public String getNameEnglish(int position) {
        return names[position];
    }

    public String getNameArabic(int position) {
        return namesArabic[position];
    }

    public String getNameMeaning(int position) {
        return namesMeaning[position];
    }

    public String getNameDetails(int position) {
        return namesDetailMeaning[position];
    }

    public ArrayList<NamesModel> getNamesData() {

        ArrayList<NamesModel> dataList = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            NamesModel data = new NamesModel();
            data.setEng(names[i]);
            data.setArabic(namesArabic[i]);
            data.setDetails(namesDetailMeaning[i]);
            data.setMeaning(namesMeaning[i]);
            dataList.add(data);
        }

        return dataList;
    }

    public int getNamesSize() {
        return namesArabic.length;
    }

}
