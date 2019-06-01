package com.nghiatv.comicreader;

import com.nghiatv.comicreader.model.Comic;

import java.util.List;

public interface IComicLoadDone {
    void onComicLoadDoneListener(List<Comic> comicList);
}
