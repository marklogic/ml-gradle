/*
 * Copyright © 2017 MarkLogic Corporation
 */

#include <MarkLogic.h>

#include <iomanip>
#include <sstream>
#include <string>
#include <vector>
#include <stdio.h>
#include <math.h>

#ifdef _MSC_VER
#define PLUGIN_DLL __declspec(dllexport)
#else // !_MSC_VER
#define PLUGIN_DLL
#endif

using namespace marklogic;
using namespace std;

////////////////////////////////////////////////////////////////////////////////

class Variance : public AggregateUDF
{
public:
  struct Value {
    double sum, sum_sq, count;
    Value() : sum(0), sum_sq(0), count(0) {}
  } value;

public:
  AggregateUDF* clone() const { return new Variance(*this); }
  void close() { delete this; }

  void start(Sequence&, Reporter&) {}
  void finish(OutputSequence& os, Reporter& reporter);

  void map(TupleIterator& values, Reporter& reporter);
  void reduce(const AggregateUDF* _o, Reporter& reporter);

  void encode(Encoder& e, Reporter& reporter);
  void decode(Decoder& d, Reporter& reporter);

protected:
  virtual double getResult();
};

double Variance::
getResult()
{
  double mean = (value.sum / value.count);
  double variance = (value.sum_sq / value.count) - (mean * mean);

  // this can be a really small negative number due to floating point
  // rounding errors so set to zero if less than zero
  if (variance < 0) {
    variance = 0;
  }
  return variance;
}

void Variance::
finish(OutputSequence& os, Reporter& reporter)
{
  os.writeValue(getResult());
}

void Variance::
map(TupleIterator& values, Reporter& reporter)
{
  for(; !values.done(); values.next()) {
    if(!values.null(0)) {
      double v; values.value(0,v);
      value.sum += v * (double)values.frequency();
      value.sum_sq += (v * v) * (double)values.frequency();
      value.count += (double)values.frequency();
    }
  }
}

void Variance::
reduce(const AggregateUDF* _o, Reporter& reporter)
{
  const Variance *o = (const Variance*)_o;
  value.sum += o->value.sum;
  value.sum_sq += o->value.sum_sq;
  value.count += o->value.count;
}

void Variance::
encode(Encoder& e, Reporter& reporter)
{
  e.encode(&value,sizeof(Value));
}

void Variance::
decode(Decoder& d, Reporter& reporter)
{
  d.decode(&value,sizeof(Value));
}

////////////////////////////////////////////////////////////////////////////////
class Stddev : public Variance
{
public:
  AggregateUDF* clone() const { return new Stddev(*this); }

protected:
  virtual double getResult();
};

double Stddev::
getResult()
{
  return sqrt(Variance::getResult());
}

////////////////////////////////////////////////////////////////////////////////

extern "C" PLUGIN_DLL void
marklogicPlugin(Registry& r)
{
  r.version();
  r.registerAggregate<Variance>("variance");
  r.registerAggregate<Stddev>("stddev");
}
