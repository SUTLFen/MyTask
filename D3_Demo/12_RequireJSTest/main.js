/**
 * Created by Fairy_LFen on 2017/5/14.
 */
require.config({
    paths: {
        jquery: 'jquery-2.0.3'
    }
});
require(['jquery'], function($){
    alert($().jquery);
});