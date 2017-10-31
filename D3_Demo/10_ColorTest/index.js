/**
 * Created by Fairy_LFen on 2017/5/8.
 */
$(function(){
    var svg = d3.select("body")
        .append("svg")
        .attr("width", 800)
        .attr("height", 600);

    var linearGradient = svg.append("defs")
        .append("linearGradient")
        .attr("id", "linear-gradient")
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "100%")
        .attr("y2", "0%");

    var stop1 = linearGradient.append("stop")
        .attr("offset", "0%")
        .attr("stop-color", "#e0ecf4"); //light blue

    var stop2 = linearGradient.append("stop")
        .attr("offset", "100%")
        .attr("stop-color", "#9ebcda"); //dark blue

    svg.append("rect")
        .attr("width", 300)
        .attr("height", 20)
        .attr("transform", "translate(" + 100 + "," + 200 +")")
        .style("fill", "url(#linear-gradient)");
});