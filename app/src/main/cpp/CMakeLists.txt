cmake_minimum_required(VERSION 3.22.1)

project(bml_native)

add_library(bml_native SHARED loader.cpp)

target_link_libraries(bml_native log)
