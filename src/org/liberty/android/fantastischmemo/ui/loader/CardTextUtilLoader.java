/*
Copyright (C) 2013 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.ui.loader;

import javax.inject.Inject;

import org.liberty.android.fantastischmemo.utils.CardTextUtil;
import org.liberty.android.fantastischmemo.utils.CardTextUtilFactory;

import roboguice.RoboGuice;
import roboguice.inject.ContextScope;
import android.content.Context;

public class CardTextUtilLoader extends
        DBLoader<CardTextUtil> {

    private CardTextUtilFactory cardTextUtilFactory;

    public CardTextUtilLoader(Context context, String dbPath) {
        super(context, dbPath);
    }

    @Inject
    public void setCardTextUtilFactory(CardTextUtilFactory cardTextUtilFactory) {
        this.cardTextUtilFactory = cardTextUtilFactory;
    }

    @Override
    public CardTextUtil dbLoadInBackground() {
        ContextScope scope = RoboGuice.getInjector(getContext()).getInstance(ContextScope.class);

        // Make sure the method is running under the context
        // The AsyncTask thread does not have the context, so we need
        // to manually enter the scope.
        synchronized(ContextScope.class) {
            scope.enter(getContext());
            try {
                CardTextUtil cardTextUtil = cardTextUtilFactory.create(dbPath);
                return cardTextUtil;
            } finally {
                scope.exit(getContext());
            }
        }
    }
}
