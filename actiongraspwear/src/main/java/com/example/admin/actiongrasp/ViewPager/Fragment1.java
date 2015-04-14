package com.example.admin.actiongrasp.ViewPager;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.actiongrasp.R;

public class Fragment1  extends Fragment {

	   @Override
       public View onCreateView(LayoutInflater inflater, ViewGroup container,
               Bundle savedInstanceState) {
           return inflater.inflate(R.layout.pic1, container, false);
       }
	   
	   public static void stopCycling() {
			// TODO Auto-generated method stub
			
		}
   }
