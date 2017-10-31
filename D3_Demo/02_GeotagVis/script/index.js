/**
 * Created by Fairy_LFEn on 2016/4/9/0009.
 */
var s;
var map;   //leaflet地图
var mySVG; //leaflet地图上叠加SVG

var initMapWithGaode = function(){
    var normalm = L.tileLayer.chinaProvider('GaoDe.Normal.Map',{maxZoom:18,minZoom:5});
    var normal = L.layerGroup([normalm]);

    map = L.map("map",{
        center:[30.3, 120.2],
        zoom:10,
        layers:[normal],
        zoomControl:false
    });

    var control = L.control.selectBox({modal: true});
    map.addControl(control);
};

var addSVGToMap = function () {

    mySVG = d3.select("#map").append("svg")
        .attr("class", "mapSvg")
        .attr("width", $("#map").width())
        .attr("height", $("#map").height());

}
$(function(){
    //使用高德layout叠加在leaflet层。。。
    initMapWithGaode();
    //在leaflet和高德地图山叠加SVG层。。。
    // addSVGToMap();
    //绘制泰森叠加层。。。
    drawVoronoiLayout();
    drawODTrajectory();

    // drawWeiboAllCount();
    // indivisualTrajectory();
});



