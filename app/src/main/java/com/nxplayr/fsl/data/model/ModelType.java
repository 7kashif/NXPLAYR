package com.nxplayr.fsl.data.model;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class ModelType {


    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int AUDIO_TYPE=2;
    public static final int CheckIn_TYPE=3;
    public static final int Poll_TYPE=4;
    public static final int Poll_TYPE1=41;
    public static final int Video_TYPE=5;
    public static final int Loder_TYPE=6;
    public static final int Friend_Suggestion_TYPE=7;
    public static final int Business_Suggestion_TYPE=8;

    public static final int Like_TYPE=9;
    public static final int DisLike_TYPE=10;
    public static final int Shared_TYPE=11;
    public static final int Comment_TYPE=12;
    public static final int Favorites_TYPE=13;
    public static final int other_user_profile=14;
    public static final int MyProfile_TYPE=15;
    public static final int threeimg=3;
    public static final int fouorimg=4;

    public int type;
    public int img,img1,img2,img3;
    public String text;



    public ModelType(int type, String text)
    {
        this.type=type;
        this.text=text;

    }
    public ModelType(int type, int img)
    {
        this.type=type;
        this.img=img;

    }
    public ModelType(int type, int img, int img1, int img2, int img3)
    {
        this.type=type;
        this. img=img;
        this. img2=img2;
        this. img1=img1;
        this. img3=img3;

    }
    public ModelType(int type, int img, int img1)
    {
        this.type=type;
        this. img=img;
        this. img1=img1;

    }
    public ModelType(int type, int img, int img1, int img2)
    {
        this.type=type;
        this. img=img;
        this. img2=img2;
        this. img1=img1;


    }

}
