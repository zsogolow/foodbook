package foodbook.thinmint.activities.unused;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foodbook.thinmint.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserProfileFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    private static final String ARG_USERID = "userid";

    private String mUserId;

    private OnUserProfileFragmentDataListener mListener;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @return A new instance of fragment UserProfileFragment.
     */
    public static UserProfileFragment newInstance(String userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ViewPager viewPager = (ViewPager) inflated.findViewById(R.id.view_pager);
        viewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) inflated.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mListener.onMyProfileFragmentCreated(inflated);

        return inflated;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserProfileFragmentDataListener) {
            mListener = (OnUserProfileFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserProfileFragmentDataListener");
        }

        mFragmentPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mUserId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUserProfileFragmentDataListener {
        void onMyProfileFragmentCreated(View view);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private static String[] NAMES = {"Notes", "Info"};
        private String mUserId;

        private UserNotesFragment mUserNotesFragment;
        private UserInfoFragment mUserInfoFragment;

        public MyPagerAdapter(FragmentManager fragmentManager, String userid) {
            super(fragmentManager);
            mUserId = userid;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UserNotesFragment.newInstance(mUserId);
                case 1:
                    return UserInfoFragment.newInstance(mUserId);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return NAMES[position];
        }
    }
}
