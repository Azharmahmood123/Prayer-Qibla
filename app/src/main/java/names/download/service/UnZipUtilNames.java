package names.download.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.quranreading.helper.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class UnZipUtilNames extends AsyncTask<String, Void, Boolean> {
	int reciter;
	boolean status = false;
	UnzipListenerNames listener;

	public void setReciter(int reciter) {
		this.reciter = reciter;
	}

	public void setListener(UnzipListenerNames listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try
		{
			unzip();

		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		listener.unzipStatus(status);
	}

	private void unzip() {
		String _zipFile = Constants.rootPathNames.getAbsolutePath() + "/" + Constants.NAMES_ZIP_TEMP;

		try
		{
			FileInputStream fin = new FileInputStream(_zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;

			while ((ze = zin.getNextEntry()) != null)
			{
				Log.v("Decompress", "Unzipping " + ze.getName());

				String name = ze.getName().toString().replaceAll("names/", "");

				File tempFile = new File(Constants.rootPathNames.getAbsolutePath(), name);

				if(!tempFile.exists())
				{
					FileOutputStream fout = new FileOutputStream(Constants.rootPathNames.getAbsolutePath() + "/" + name);

					byte[] buffer = new byte[8192];
					int len;

					while ((len = zin.read(buffer)) != -1)
					{
						fout.write(buffer, 0, len);
					}

					fout.close();
				}

				zin.closeEntry();
			}

			zin.close();

			status = true;

			File tempFile = new File(_zipFile);

			if(tempFile.exists())
			{
				tempFile.delete();
			}
		}
		catch (Exception e)
		{
			Log.e("Decompress", "unzip " + e.toString());
			status = false;
		}
	}
}
