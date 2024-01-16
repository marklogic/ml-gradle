PLUGIN_NAME = varianceplugin
PLUGIN_VERSION = 0.1
PLUGIN_PROVIDER = MarkLogic
PLUGIN_DESCRIPTION = variance native plugin

PLUGIN_SRCS = \
	VariancePlugin.cpp

MANIFEST = manifest.xml

ZIP_NAME = $(PLUGIN_NAME).zip
PLUGIN_OBJS = ${PLUGIN_SRCS:.cpp=.o}

all:: $(PLUGIN_NAME)

ifneq "$(WINDIR)" ""

-include defs.win32

CPPFLAGS = \
	-nologo \
	-W3 \
	-GR \
	-GS- \
	-EHsc \
	-wd4996 \
	-Zi -MTd -bigobj \
	-O2 -Oy- \
	-D_WIN32_WINNT=0x501 \
	-D_SECURE_SCL=0

FULL_LIB_NAME = $(PLUGIN_NAME).dll

DLL_LIBS="$(ProgramW6432)/MarkLogic/MarkLogic.lib"
INCLUDES=-I "$(ProgramW6432)/MarkLogic/include"

$(FULL_LIB_NAME): $(PLUGIN_OBJS)
	$(LINK) $(PLUGIN_OBJS) $(DLL_LIBS) \
		-nologo -incremental:no -dll -out:$@ -largeaddressaware

%.o: %.cpp
	$(CPP) $(CPPFLAGS) $(INCLUDES) -Fo$@ -I. -c $<

vsinstalls: makevcinclude.sh findvcinstalls.cmd
	rm -rf $@ vsinstalls
	./findvcinstalls.cmd bash ./makevcinclude.sh > $@

clean::
	rm -rf $(FULL_LIB_NAME:.dll=.lib)
	rm -rf $(FULL_LIB_NAME).manifest
	rm -rf $(FULL_LIB_NAME:.dll=.exp)
	rm -rf $(FULL_LIB_NAME:.dll=.pdb)
	rm -rf ${PLUGIN_SRCS:.cpp=.lib}
	rm -rf ${PLUGIN_SRCS:.cpp=.exp}
	rm -rf ${PLUGIN_SRCS:.cpp=.pdb}
	rm -rf *.pdb
	rm -rf vsinstalls
else

ifeq "$(shell uname -s)" "Linux"
MARKLOGIC = /opt/MarkLogic
FULL_LIB_NAME = lib$(PLUGIN_NAME).so
CPP=g++
LIB_FLAGS = -m64 -fPIC -shared
endif

ifeq "$(shell uname -s)" "Darwin"
MARKLOGIC := $(shell cd ~; pwd)/Library/MarkLogic
FULL_LIB_NAME = lib$(PLUGIN_NAME).dylib
CPP=g++
LIB_FLAGS = -m64 -dynamiclib -Wl,-undefined,dynamic_lookup
endif

$(FULL_LIB_NAME): $(PLUGIN_OBJS)
	$(CPP) $(LIB_FLAGS) -o $(FULL_LIB_NAME) $(PLUGIN_OBJS)

EXE_EXTRA = -ldl
EXE_FLAGS = -rdynamic

%.o: %.cpp
	$(CPP) -m64 -fPIC -g -O2 -Woverloaded-virtual -I. -I$(MARKLOGIC)/include -o $@ -c $<
endif

clean::
	rm -rf $(ZIP_NAME)
	rm -rf $(FULL_LIB_NAME)
	rm -rf $(PLUGIN_OBJS)
	rm -rf $(MANIFEST)

$(MANIFEST):
	echo '<?xml version="1.0" encoding="UTF-8"?>' > $(MANIFEST)
	echo '<plugin xmlns="http://marklogic.com/extension/plugin">' >> $(MANIFEST)
	echo '  <name>$(PLUGIN_NAME)</name>' >> $(MANIFEST)
	echo '  <id>$(PLUGIN_NAME)</id>' >> $(MANIFEST)
	echo '  <version>$(PLUGIN_VERSION)</version>' >> $(MANIFEST)
	echo '  <provider-name>$(PLUGIN_PROVIDER)</provider-name>' >> $(MANIFEST)
	echo '  <description>$(PLUGIN_DESCRIPTION)</description>' >> $(MANIFEST)
	echo '  <native>' >> $(MANIFEST)
	echo '    <path>$(FULL_LIB_NAME)</path>' >> $(MANIFEST)
	echo '  </native>' >> $(MANIFEST)
	echo '</plugin>' >> $(MANIFEST)

$(ZIP_NAME): $(FULL_LIB_NAME) $(MANIFEST)
	zip $(ZIP_NAME) $(FULL_LIB_NAME) $(MANIFEST)

$(PLUGIN_NAME): $(ZIP_NAME)

.PHONY: $(PLUGIN_NAME)
