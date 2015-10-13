/******************************************************************************
 * Copyright (c) 2008-2014 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 ******************************************************************************/

package nu.mine.kino.entity;

public class Task2EVViewBean {

    /**
     * 引数のオブジェクトのプロパティからデータをコピーして戻り値のオブジェクトを生成して返すメソッド。
     * 
     * @param source
     * @return
     */
    public static EVViewBean convert(Task source) {
        EVViewBean dest = new EVViewBean();

        // 必要に応じて特殊な載せ替え処理 開始
        ((EVViewBean) dest).setId(source.getId());
        ((EVViewBean) dest).setTaskId(source.getTaskId());
        ((EVViewBean) dest).setType(source.getType());
        ((EVViewBean) dest).setTaskSharp(source.getTaskSharp());
        ((EVViewBean) dest).setTaskName(source.getTaskName());
        ((EVViewBean) dest).setPersonInCharge(source.getPersonInCharge());
        ((EVViewBean) dest).setTaskAbstract(source.getTaskAbstract());
        ((EVViewBean) dest).setNumberOfManDays(source.getNumberOfManDays());
        ((EVViewBean) dest).setScheduledStartDate(source
                .getScheduledStartDate());
        ((EVViewBean) dest).setScheduledEndDate(source.getScheduledEndDate());
        ((EVViewBean) dest).setNumberOfDays(source.getNumberOfDays());

        // 特殊な載せ替え処理 終了

        return dest;
    }

    /**
     * 第一引数から第二引数へプロパティをコピーするメソッド。
     * 
     * @param source
     * @param dest
     */
    public static void convert(Task source, EVViewBean dest) {
        // 必要に応じて特殊な載せ替え処理 開始
        ((EVViewBean) dest).setId(source.getId());
        ((EVViewBean) dest).setTaskId(source.getTaskId());
        ((EVViewBean) dest).setType(source.getType());
        ((EVViewBean) dest).setTaskSharp(source.getTaskSharp());
        ((EVViewBean) dest).setTaskName(source.getTaskName());
        ((EVViewBean) dest).setPersonInCharge(source.getPersonInCharge());
        ((EVViewBean) dest).setTaskAbstract(source.getTaskAbstract());
        ((EVViewBean) dest).setNumberOfManDays(source.getNumberOfManDays());
        ((EVViewBean) dest).setScheduledStartDate(source
                .getScheduledStartDate());
        ((EVViewBean) dest).setScheduledEndDate(source.getScheduledEndDate());
        ((EVViewBean) dest).setNumberOfDays(source.getNumberOfDays());

        // 特殊な載せ替え処理 終了

    }

}
