					/* In the name of GOD, the Most Gracious, the Most Merciful */
/*
 *	Date : 27th of May 2011
 *	Description : Read from relevant XML file and return data
 *
 * 	Author	: Jazarine Jamal
 *  E-Mail 	: jazarinester@gmail.com
 *  Web		: http://www.jazarine.org
 * */
package quran.adapter;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
 
public class AudioTimeXMLParser
{
	public static ArrayList<Integer> getTranslatedAyaList(Context context,XmlPullParser xpp,int position)
	{
		ArrayList<Integer> ayahList = new ArrayList<Integer>();
		try 
		{
			boolean suraFound=false;
			boolean suraFoundandFinishedGet = false;
			
			while ((xpp.getEventType()!=XmlPullParser.END_DOCUMENT))
			{
				if (xpp.getEventType()==XmlPullParser.START_TAG)
				{
					if(xpp.getName().equals("sura"))
					{
						if(xpp.getAttributeValue(0).equals(position+""))
						{
							xpp.nextTag();
							suraFound = true;
							suraFoundandFinishedGet=false;
							continue;
						}
						else
						{
							if (suraFound)
							{
								suraFoundandFinishedGet = true;
							}
						}
					}
					if(suraFoundandFinishedGet)
					{
						break;
					}
					if (xpp.getName().equals("aya") && suraFound)
					{
						int time = Integer.parseInt(xpp.getAttributeValue(1).toString());
						ayahList.add(time);
					}
				}
				xpp.next();
			}
		}
		catch(Exception ex)
		{
			return ayahList;
		}
		return ayahList;
	}
}